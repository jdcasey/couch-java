/*******************************************************************************
 * Copyright 2011 John Casey
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package org.commonjava.couch.db;

import static org.apache.commons.io.IOUtils.closeQuietly;
import static org.apache.commons.io.IOUtils.copy;
import static org.apache.http.HttpStatus.SC_CREATED;
import static org.apache.http.HttpStatus.SC_NOT_FOUND;
import static org.apache.http.HttpStatus.SC_OK;
import static org.commonjava.couch.util.UrlUtils.buildUrl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.enterprise.event.Event;
import javax.enterprise.inject.Alternative;
import javax.inject.Named;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.entity.StringEntity;
import org.apache.log4j.Logger;
import org.commonjava.couch.change.j2ee.ApplicationEvent;
import org.commonjava.couch.change.j2ee.DatabaseEvent;
import org.commonjava.couch.conf.CouchDBConfiguration;
import org.commonjava.couch.db.action.BulkActionHolder;
import org.commonjava.couch.db.action.CouchDocumentAction;
import org.commonjava.couch.db.action.DeleteAction;
import org.commonjava.couch.db.action.StoreAction;
import org.commonjava.couch.db.handler.SerializedGetHandler;
import org.commonjava.couch.db.model.AppDescription;
import org.commonjava.couch.db.model.AttachmentDownload;
import org.commonjava.couch.db.model.CouchDocRefSet;
import org.commonjava.couch.db.model.CouchObjectList;
import org.commonjava.couch.db.model.ViewRequest;
import org.commonjava.couch.io.CouchAppReader;
import org.commonjava.couch.io.CouchHttpClient;
import org.commonjava.couch.io.Serializer;
import org.commonjava.couch.io.json.CouchObjectListDeserializer;
import org.commonjava.couch.model.Attachment;
import org.commonjava.couch.model.CouchApp;
import org.commonjava.couch.model.CouchDocRef;
import org.commonjava.couch.model.CouchDocument;
import org.commonjava.couch.model.CouchError;
import org.commonjava.couch.model.DenormalizedCouchDoc;
import org.commonjava.couch.util.ToString;

@Named( "dont-use-directly" )
@Alternative
public class CouchManager
{

    private static final Logger LOGGER = Logger.getLogger( CouchManager.class );

    private static final String REV = "rev";

    private static final String VIEW_BASE = "_view";

    private static final String APP_BASE = "_design";

    private static final String BULK_DOCS = "_bulk_docs";

    private static final String ALL_DOCS = "_all_docs";

    private ExecutorService exec;

    private final CouchAppReader appReader;

    private final CouchDBConfiguration config;

    private final CouchHttpClient client;

    private Event<DatabaseEvent> dbEvent;

    private Event<ApplicationEvent> appEvent;

    private final Serializer serializer;

    public CouchManager( final CouchDBConfiguration config, final CouchHttpClient client, final Serializer serializer,
                         final CouchAppReader appReader )
    {
        this.config = config;
        this.client = client;
        this.serializer = serializer;
        this.appReader = appReader;
    }

    public CouchManager( final CouchDBConfiguration config )
    {
        this.config = config;
        this.serializer = new Serializer();
        this.appReader = new CouchAppReader();
        this.client = new CouchHttpClient( config, serializer );
    }

    public CouchManager( final CouchDBConfiguration config, final CouchHttpClient client, final Serializer serializer,
                         final CouchAppReader appReader, final Event<DatabaseEvent> dbEvent,
                         final Event<ApplicationEvent> appEvent )
    {
        this.config = config;
        this.client = client;
        this.serializer = serializer;
        this.appReader = appReader;
        this.dbEvent = dbEvent;
        this.appEvent = appEvent;
    }

    public void initialize( final AppDescription description )
        throws CouchDBException
    {
        CouchApp app;
        try
        {
            app = appReader.readAppDefinition( description );
        }
        catch ( final IOException e )
        {
            throw new CouchDBException( "Failed to retrieve application definition: %s. Reason: %s", e,
                                        description.getClasspathAppResource(), e.getMessage() );
        }

        if ( !dbExists() )
        {
            createDatabase();
        }
        else
        {
            LOGGER.info( "Database already exists: " + config.getDatabaseUrl() );
        }

        if ( !appExists( description.getAppName() ) )
        {
            installApplication( app );
        }
        else
        {
            LOGGER.info( "App: " + app.getCouchDocId() + " already exists in db: " + config.getDatabaseUrl() );
        }
    }

    public void store( final Collection<? extends CouchDocument> documents, final boolean skipIfExists,
                       final boolean allOrNothing )
        throws CouchDBException
    {
        final Set<StoreAction> toStore = new HashSet<StoreAction>();
        for ( final CouchDocument doc : documents )
        {
            if ( doc instanceof DenormalizedCouchDoc )
            {
                ( (DenormalizedCouchDoc) doc ).calculateDenormalizedFields();
            }

            if ( skipIfExists && documentRevisionExists( doc ) )
            {
                continue;
            }

            toStore.add( new StoreAction( doc, skipIfExists ) );
        }

        modify( toStore, allOrNothing );
        // threadedExecute( toStore, dbUrl );
    }

    public void delete( final Collection<? extends CouchDocument> documents, final boolean allOrNothing )
        throws CouchDBException
    {
        final Set<DeleteAction> toDelete = new HashSet<DeleteAction>();
        for ( final CouchDocument doc : documents )
        {
            if ( doc instanceof DenormalizedCouchDoc )
            {
                ( (DenormalizedCouchDoc) doc ).calculateDenormalizedFields();
            }

            if ( !documentRevisionExists( doc ) )
            {
                continue;
            }

            toDelete.add( new DeleteAction( doc ) );
        }

        modify( toDelete, allOrNothing );
        // threadedExecute( toDelete, dbUrl );
    }

    public void modify( final Collection<? extends CouchDocumentAction> actions, final boolean allOrNothing )
        throws CouchDBException
    {
        for ( final CouchDocumentAction action : actions )
        {
            final CouchDocument doc = action.getDocument();
            if ( doc instanceof DenormalizedCouchDoc )
            {
                ( (DenormalizedCouchDoc) doc ).calculateDenormalizedFields();
            }
        }

        final BulkActionHolder bulk = new BulkActionHolder( actions, allOrNothing );
        final String body = serializer.toString( bulk );

        String url;
        try
        {
            url = buildUrl( config.getDatabaseUrl(), (Map<String, String>) null, BULK_DOCS );
        }
        catch ( final MalformedURLException e )
        {
            throw new CouchDBException( "Failed to format bulk-update URL: %s", e, e.getMessage() );
        }

        final HttpPost request = new HttpPost( url );
        try
        {
            request.setEntity( new StringEntity( body, "application/json", "UTF-8" ) );
        }
        catch ( final UnsupportedEncodingException e )
        {
            throw new CouchDBException( "Failed to encode POST entity for bulk update: %s", e, e.getMessage() );
        }

        try
        {
            final HttpResponse response = client.executeHttpWithResponse( request, "Bulk update failed" );
            final StatusLine statusLine = response.getStatusLine();
            final int code = statusLine.getStatusCode();
            if ( code != SC_OK && code != SC_CREATED )
            {
                String content = null;
                final HttpEntity entity = response.getEntity();
                if ( entity != null )
                {
                    final ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    InputStream in = null;
                    try
                    {
                        in = entity.getContent();
                        copy( in, baos );
                    }
                    catch ( final IOException e )
                    {
                        throw new CouchDBException( "Error reading response content for error: %s\nError was: %s", e,
                                                    statusLine, e.getMessage() );
                    }
                    finally
                    {
                        closeQuietly( in );
                    }

                    content = new String( baos.toByteArray() );
                }

                throw new CouchDBException( "Bulk operation failed. Status line: %s\nContent:\n----------\n\n%s",
                                            statusLine, content );
            }
        }
        finally
        {
            client.cleanup( request );
        }

        // threadedExecute( new HashSet<CouchDocumentAction>( actions ), dbUrl );
    }

    public <T extends CouchDocument> List<T> getViewListing( final ViewRequest req, final Class<T> itemType )
        throws CouchDBException
    {
        req.setParameter( ViewRequest.INCLUDE_DOCS, true );

        final String url = buildViewUrl( req );

        if ( LOGGER.isDebugEnabled() )
        {
            LOGGER.debug( "Retrieving view listing from: " + url );
        }

        final HttpGet request = new HttpGet( url );

        final CouchObjectListDeserializer<T> deser = new CouchObjectListDeserializer<T>( itemType, false );

        final CouchObjectList<T> listing =
            client.executeHttpAndReturn( request, deser.typeLiteral(),
                                         new ToString( "Failed to retrieve contents for view request: %s", req ), deser );

        for ( final T t : listing )
        {
            if ( t instanceof DenormalizedCouchDoc )
            {
                ( (DenormalizedCouchDoc) t ).calculateDenormalizedFields();
            }

        }

        return listing.getItems();
    }

    public <V> V getView( final ViewRequest req, final Class<V> type )
        throws CouchDBException
    {
        final String url = buildViewUrl( req );
        final HttpGet request = new HttpGet( url );
        return client.executeHttpAndReturn( request, type,
                                            new ToString( "Failed to retrieve contents for view request: %s", req ) );
    }

    public <T extends CouchDocument> List<T> getDocuments( final Class<T> docType, final Set<CouchDocRef> refs )
        throws CouchDBException
    {
        final CouchDocRefSet refSet = new CouchDocRefSet( refs );
        return getDocuments( docType, refSet );
    }

    public <T extends CouchDocument> List<T> getDocuments( final Class<T> docType, final CouchDocRef... refs )
        throws CouchDBException
    {
        return getDocuments( docType, false, refs );
    }

    public <T extends CouchDocument> List<T> getDocuments( final Class<T> docType, final boolean allowMissing,
                                                           final CouchDocRef... refs )
        throws CouchDBException
    {
        if ( refs == null || refs.length < 1 )
        {
            return null;
        }

        final CouchDocRefSet refSet = new CouchDocRefSet( refs );
        return getDocuments( docType, refSet, allowMissing );
    }

    public <T extends CouchDocument> List<T> getDocuments( final Class<T> docType, final CouchDocRefSet refSet )
        throws CouchDBException
    {
        return getDocuments( docType, refSet, false );
    }

    public <T extends CouchDocument> List<T> getDocuments( final Class<T> docType, final CouchDocRefSet refSet,
                                                           final boolean allowMissing )
        throws CouchDBException
    {
        String url;
        try
        {
            url =
                buildUrl( config.getDatabaseUrl(), Collections.singletonMap( ViewRequest.INCLUDE_DOCS, "true" ),
                          ALL_DOCS );
        }
        catch ( final MalformedURLException e )
        {
            throw new CouchDBException( "Failed to format multi-doc URL: %s", e, e.getMessage() );
        }

        if ( LOGGER.isDebugEnabled() )
        {
            LOGGER.debug( "Selecting multiple documents from: " + url );
        }

        final HttpPost request = new HttpPost( url );
        try
        {
            final String body = serializer.toString( refSet );
            request.setEntity( new StringEntity( body, "application/json", "UTF-8" ) );
        }
        catch ( final UnsupportedEncodingException e )
        {
            throw new CouchDBException( "Failed to encode POST entity for multi-document selection: %s", e,
                                        e.getMessage() );
        }

        final CouchObjectListDeserializer<T> deser = new CouchObjectListDeserializer<T>( docType, allowMissing );

        final CouchObjectList<T> listing =
            client.executeHttpAndReturn( request, deser.typeLiteral(),
                                         new ToString( "Failed to retrieve documents for: %s", refSet ), deser );

        for ( final T t : listing )
        {
            if ( t instanceof DenormalizedCouchDoc )
            {
                ( (DenormalizedCouchDoc) t ).calculateDenormalizedFields();
            }

        }

        return listing.getItems();
    }

    public <T> T getDocument( final CouchDocRef ref, final Class<T> docType )
        throws CouchDBException
    {
        if ( !documentRevisionExists( ref ) )
        {
            return null;
        }

        final String url = buildDocUrl( ref, true );
        final HttpGet get = new HttpGet( url );

        final T result =
            client.executeHttpAndReturn( get, new SerializedGetHandler<T>( serializer, docType ),
                                         new ToString( "Failed to retrieve document: %s", ref ) );

        if ( result instanceof DenormalizedCouchDoc )
        {
            ( (DenormalizedCouchDoc) result ).calculateDenormalizedFields();
        }

        return result;
    }

    public boolean store( final CouchDocument doc, final boolean skipIfExists )
        throws CouchDBException
    {
        if ( doc instanceof DenormalizedCouchDoc )
        {
            ( (DenormalizedCouchDoc) doc ).calculateDenormalizedFields();
        }

        if ( skipIfExists && documentRevisionExists( doc ) )
        {
            return false;
        }

        final HttpPost request = new HttpPost( config.getDatabaseUrl() );
        try
        {
            request.setHeader( "Referer", config.getDatabaseUrl() );
            final String src = serializer.toString( doc );
            request.setEntity( new StringEntity( src, "application/json", "UTF-8" ) );

            client.executeHttp( request, SC_CREATED, "Failed to store document" );
        }
        catch ( final UnsupportedEncodingException e )
        {
            throw new CouchDBException( "Failed to store document: %s.\nReason: %s", e, doc, e.getMessage() );
        }

        return true;
    }

    public void delete( final CouchDocument doc )
        throws CouchDBException
    {
        if ( doc instanceof DenormalizedCouchDoc )
        {
            ( (DenormalizedCouchDoc) doc ).calculateDenormalizedFields();
        }

        if ( !documentRevisionExists( doc ) )
        {
            return;
        }

        final String url = buildDocUrl( doc, true );
        final HttpDelete request = new HttpDelete( url );
        client.executeHttp( request, SC_OK, "Failed to delete document" );
    }

    public void attach( final CouchDocument doc, final Attachment attachment )
        throws CouchDBException
    {
        if ( !documentRevisionExists( doc ) )
        {
            throw new CouchDBException( "Cannot attach to a non-existent document: %s", doc.getCouchDocId() );
        }

        String url;
        try
        {
            url =
                buildUrl( config.getDatabaseUrl(), Collections.singletonMap( REV, doc.getCouchDocRev() ),
                          doc.getCouchDocId(), attachment.getName() );
        }
        catch ( final MalformedURLException e )
        {
            throw new CouchDBException( "Failed to format attachment URL for: %s to document: %s. Error: %s", e,
                                        attachment.getName(), doc.getCouchDocId(), e.getMessage() );
        }

        LOGGER.info( "Attaching " + attachment.getName() + " to document: " + doc.getCouchDocId() + "\nURL: " + url );

        final HttpPut request = new HttpPut( url );
        request.setHeader( HttpHeaders.CONTENT_TYPE, attachment.getContentType() );

        try
        {
            request.setEntity( new InputStreamEntity( attachment.getData(), attachment.getContentLength() ) );
        }
        catch ( final IOException e )
        {
            throw new CouchDBException( "Failed to read attachment data: %s. Error: %s", e, attachment.getName(),
                                        e.getMessage() );
        }

        client.executeHttp( request, SC_CREATED, "Failed to attach to document" );
    }

    public void deleteAttachment( final CouchDocument doc, final String attachmentName )
        throws CouchDBException
    {
        doc.setCouchDocRev( null );
        if ( !documentRevisionExists( doc ) )
        {
            throw new CouchDBException( "Cannot delete attachment from a non-existent document: %s",
                                        doc.getCouchDocId() );
        }

        String url;
        try
        {
            url =
                buildUrl( config.getDatabaseUrl(), Collections.singletonMap( REV, doc.getCouchDocRev() ),
                          doc.getCouchDocId(), attachmentName );
        }
        catch ( final MalformedURLException e )
        {
            throw new CouchDBException( "Failed to format attachment URL for: %s to document: %s. Error: %s", e,
                                        attachmentName, doc.getCouchDocId(), e.getMessage() );
        }

        final HttpDelete request = new HttpDelete( url );
        client.executeHttp( request, SC_OK, "Failed to delete attachment" );
    }

    public Attachment getAttachment( final CouchDocument doc, final String attachmentName )
        throws CouchDBException
    {
        String url;
        try
        {
            url = buildUrl( config.getDatabaseUrl(), doc.getCouchDocId(), attachmentName );
        }
        catch ( final MalformedURLException e )
        {
            throw new CouchDBException( "Failed to format attachment URL for: %s to document: %s. Error: %s", e,
                                        attachmentName, doc.getCouchDocId(), e.getMessage() );
        }

        final HttpGet request = new HttpGet( url );
        final HttpResponse response = client.executeHttpWithResponse( request, "Failed to retrieve attachment." );

        if ( response.getStatusLine()
                     .getStatusCode() == SC_NOT_FOUND )
        {
            return null;
        }
        else if ( response.getStatusLine()
                          .getStatusCode() != SC_OK )
        {
            throw new CouchDBException( "Failed to retrieve attachment: %s from: %s. Reason: %s", attachmentName,
                                        doc.getCouchDocId(), response.getStatusLine() );
        }

        return new AttachmentDownload( attachmentName, request, response, client );
    }

    public boolean viewExists( final String appName, final String viewName )
        throws CouchDBException
    {
        try
        {
            return exists( buildUrl( config.getDatabaseUrl(), (Map<String, String>) null, APP_BASE, appName, VIEW_BASE,
                                     viewName ) );
        }
        catch ( final MalformedURLException e )
        {
            throw new CouchDBException( "Cannot format view URL for: %s in: %s. Reason: %s", e, viewName, appName,
                                        e.getMessage() );
        }
        catch ( final CouchDBException e )
        {
            throw new CouchDBException( "Cannot verify existence of view: %s in: %s. Reason: %s", e, viewName, appName,
                                        e.getMessage() );
        }
    }

    public boolean appExists( final String appName )
        throws CouchDBException
    {
        try
        {
            return exists( buildUrl( config.getDatabaseUrl(), (Map<String, String>) null, APP_BASE, appName ) );
        }
        catch ( final MalformedURLException e )
        {
            throw new CouchDBException( "Cannot format application URL: %s. Reason: %s", e, appName, e.getMessage() );
        }
        catch ( final CouchDBException e )
        {
            throw new CouchDBException( "Cannot verify existence of application: %s. Reason: %s", e, appName,
                                        e.getMessage() );
        }
    }

    public boolean documentRevisionExists( final CouchDocument doc )
        throws CouchDBException
    {
        if ( doc instanceof DenormalizedCouchDoc )
        {
            ( (DenormalizedCouchDoc) doc ).calculateDenormalizedFields();
        }

        final String docUrl = buildDocUrl( doc, doc.getCouchDocRev() != null );
        boolean exists = false;

        final HttpHead request = new HttpHead( docUrl );
        try
        {
            final HttpResponse response = client.executeHttpWithResponse( request, "Failed to ping database URL" );

            final StatusLine statusLine = response.getStatusLine();
            if ( statusLine.getStatusCode() == SC_OK )
            {
                exists = true;
            }
            else if ( statusLine.getStatusCode() != SC_NOT_FOUND )
            {
                final HttpEntity entity = response.getEntity();
                CouchError error;

                try
                {
                    error = serializer.toError( entity );
                }
                catch ( final IOException e )
                {
                    throw new CouchDBException(
                                                "Failed to ping database URL: %s.\nReason: %s\nError: Cannot read error status: %s",
                                                e, docUrl, statusLine, e.getMessage() );
                }

                throw new CouchDBException( "Failed to ping database URL: %s.\nReason: %s\nError: %s", docUrl,
                                            statusLine, error );
            }

            if ( exists )
            {
                final Header etag = response.getFirstHeader( "Etag" );
                String rev = etag.getValue();
                if ( rev.startsWith( "\"" ) || rev.startsWith( "'" ) )
                {
                    rev = rev.substring( 1 );
                }

                if ( rev.endsWith( "\"" ) || rev.endsWith( "'" ) )
                {
                    rev = rev.substring( 0, rev.length() - 1 );
                }

                doc.setCouchDocRev( rev );
            }
        }
        finally
        {
            client.cleanup( request );
        }

        return exists;
    }

    public boolean exists( final CouchDocument doc )
        throws CouchDBException
    {
        if ( doc instanceof DenormalizedCouchDoc )
        {
            ( (DenormalizedCouchDoc) doc ).calculateDenormalizedFields();
        }

        final String docUrl = buildDocUrl( doc, false );
        return exists( docUrl );
    }

    public boolean exists( final String path )
        throws CouchDBException
    {
        boolean exists = false;

        String url;
        try
        {
            url = buildUrl( config.getDatabaseUrl(), path );
        }
        catch ( final MalformedURLException e )
        {
            throw new CouchDBException( "Invalid path: %s. Reason: %s", e, path, e.getMessage() );
        }

        final HttpHead request = new HttpHead( url );
        try
        {
            final HttpResponse response = client.executeHttpWithResponse( request, "Failed to ping database URL" );

            final StatusLine statusLine = response.getStatusLine();
            if ( statusLine.getStatusCode() == SC_OK )
            {
                exists = true;
            }
            else if ( statusLine.getStatusCode() != SC_NOT_FOUND )
            {
                final HttpEntity entity = response.getEntity();
                CouchError error;

                try
                {
                    error = serializer.toError( entity );
                }
                catch ( final IOException e )
                {
                    throw new CouchDBException(
                                                "Failed to ping database URL: %s.\nReason: %s\nError: Cannot read error status: %s",
                                                e, url, statusLine, e.getMessage() );
                }

                throw new CouchDBException( "Failed to ping database URL: %s.\nReason: %s\nError: %s", url, statusLine,
                                            error );
            }
        }
        finally
        {
            client.cleanup( request );
        }

        return exists;
    }

    public boolean dbExists()
        throws CouchDBException
    {
        return exists( "/" );
    }

    public void dropDatabase()
        throws CouchDBException
    {
        if ( !dbExists() )
        {
            return;
        }

        final HttpDelete request = new HttpDelete( config.getDatabaseUrl() );
        client.executeHttp( request, SC_OK, "Failed to drop database" );
        fireDBEvent( DatabaseEvent.Type.DROP, config.getDatabaseUrl() );
    }

    public void createDatabase()
        throws CouchDBException
    {
        LOGGER.info( "Creating database: " + config.getDatabaseUrl() );
        final HttpPut request = new HttpPut( config.getDatabaseUrl() );
        client.executeHttp( request, SC_CREATED, "Failed to create database" );
        fireDBEvent( DatabaseEvent.Type.CREATE, config.getDatabaseUrl() );
    }

    public void installApplication( final CouchApp app )
        throws CouchDBException
    {
        final String url = buildDocUrl( app, true );
        LOGGER.info( "Installing app at: " + url );

        final HttpPut request = new HttpPut( url );
        try
        {
            request.setHeader( "Referer", config.getDatabaseUrl() );
            final String appJson = serializer.toString( app );
            request.setEntity( new StringEntity( appJson, "application/json", "UTF-8" ) );

            client.executeHttp( request, SC_CREATED, "Failed to store application document" );
            fireAppEvent( ApplicationEvent.Type.INSTALL, app.getDescription() );
        }
        catch ( final UnsupportedEncodingException e )
        {
            throw new CouchDBException( "Failed to store application document: %s.\nReason: %s", e, app, e.getMessage() );
        }
    }

    protected String buildViewUrl( final ViewRequest req )
        throws CouchDBException
    {
        try
        {
            return buildUrl( config.getDatabaseUrl(), req.getRequestParameters(), APP_BASE, req.getApplication(),
                             VIEW_BASE, req.getView() );
        }
        catch ( final MalformedURLException e )
        {
            throw new CouchDBException( "Failed to format view URL for: %s.\nReason: %s", e, req, e.getMessage() );
        }
    }

    protected String buildDocUrl( final CouchDocument doc, final boolean includeRevision )
        throws CouchDBException
    {
        if ( doc instanceof DenormalizedCouchDoc )
        {
            ( (DenormalizedCouchDoc) doc ).calculateDenormalizedFields();
        }

        try
        {
            String url;
            if ( includeRevision && doc.getCouchDocRev() != null )
            {
                final Map<String, String> params = Collections.singletonMap( REV, doc.getCouchDocRev() );
                url = buildUrl( config.getDatabaseUrl(), params, doc.getCouchDocId() );
            }
            else
            {
                url = buildUrl( config.getDatabaseUrl(), (Map<String, String>) null, doc.getCouchDocId() );
            }

            return url;
        }
        catch ( final MalformedURLException e )
        {
            throw new CouchDBException( "Failed to format document URL for id: %s [revision=%s].\nReason: %s", e,
                                        doc.getCouchDocId(), doc.getCouchDocRev(), e.getMessage() );
        }
    }

    protected synchronized void threadedExecute( final Set<? extends CouchDocumentAction> actions )
        throws CouchDBException
    {
        if ( exec == null )
        {
            exec = Executors.newCachedThreadPool();
        }

        final CountDownLatch latch = new CountDownLatch( actions.size() );
        for ( final CouchDocumentAction action : actions )
        {
            action.prepareExecution( latch, this );
            exec.execute( action );
        }

        synchronized ( latch )
        {
            while ( latch.getCount() > 0 )
            {
                if ( LOGGER.isDebugEnabled() )
                {
                    LOGGER.debug( "Waiting for " + latch.getCount() + " actions to complete." );
                }

                try
                {
                    latch.await( 2, TimeUnit.SECONDS );
                }
                catch ( final InterruptedException e )
                {
                    break;
                }
            }
        }

        final List<Throwable> errors = new ArrayList<Throwable>();
        for ( final CouchDocumentAction action : actions )
        {
            if ( action.getError() != null )
            {
                errors.add( action.getError() );
            }
        }

        if ( !errors.isEmpty() )
        {
            throw new CouchDBException( "Failed to execute %d actions.", errors.size() ).withNestedErrors( errors );
        }
    }

    protected CouchAppReader getAppReader()
    {
        return appReader;
    }

    private void fireDBEvent( final DatabaseEvent.Type type, final String url )
    {
        if ( dbEvent != null )
        {
            dbEvent.fire( new DatabaseEvent( type, url ) );
        }
    }

    private void fireAppEvent( final ApplicationEvent.Type type, final AppDescription description )
    {
        if ( appEvent != null )
        {
            appEvent.fire( new ApplicationEvent( type, description ) );
        }
    }

}
