package org.commonjava.maven.mdd.db;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.log4j.Logger;
import org.apache.maven.mae.project.key.FullProjectKey;
import org.apache.maven.model.Dependency;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;
import org.commonjava.maven.mdd.db.session.DependencyDBSession;
import org.commonjava.maven.mdd.model.DependencyRelationship;
import org.commonjava.maven.mdd.model.DependencyRelationshipListing;
import org.commonjava.maven.mdd.model.io.Serializer;

@Component( role = DependencyDatabase.class )
public class DependencyDatabase
{

    private static final String KEY = "key";

    private static final String INCLUDE_DOCS = "include_docs";

    private static final Logger LOGGER = Logger.getLogger( DependencyDatabase.class );

    private static final String REV = "rev";

    private HttpClient client;

    private final ExecutorService exec = Executors.newCachedThreadPool();

    @Requirement
    private Serializer serializer;

    public void validateConnection( final DependencyDBSession session )
        throws DatabaseException
    {
        HttpHead request = new HttpHead( session.getBaseUrl() );
        try
        {
            getClient().execute( request );
        }
        catch ( ClientProtocolException e )
        {
            throw new DatabaseException(
                                         "Failed to validate connection to dependency db: %s.\nReason: %s",
                                         e, session.getBaseUrl(), e.getMessage() );
        }
        catch ( IOException e )
        {
            throw new DatabaseException(
                                         "Failed to validate connection to dependency db: %s.\nReason: %s",
                                         e, session.getBaseUrl(), e.getMessage() );
        }
        finally
        {
            cleanup( request );
        }
    }

    public DependencyRelationshipListing getDirectDependencies( final FullProjectKey projectKey,
                                                                final DependencyDBSession session )
        throws DatabaseException
    {
        return getDependencyRelationshipView( "direct-dependencies", projectKey, session );
    }

    public DependencyRelationshipListing getDirectDependents( final FullProjectKey projectKey,
                                                              final DependencyDBSession session )
        throws DatabaseException
    {
        return getDependencyRelationshipView( "direct-dependents", projectKey, session );
    }

    public void store( final Collection<DependencyRelationship> rels,
                       final DependencyDBSession session )
        throws DatabaseException
    {
        Set<DependencyRelationship> relationships = new HashSet<DependencyRelationship>( rels );
        CountDownLatch latch = new CountDownLatch( relationships.size() );
        List<DatabaseException> errors = new ArrayList<DatabaseException>();

        for ( DependencyRelationship rel : relationships )
        {
            exec.execute( new StorageWorker( this, latch, rel, session, errors ) );
            // new StorageWorker( this, latch, rel, session, errors ).run();
        }

        synchronized ( latch )
        {
            while ( latch.getCount() > 0 )
            {
                LOGGER.info( "Waiting for " + latch.getCount() + " workers." );
                try
                {
                    latch.await( 3, TimeUnit.SECONDS );
                }
                catch ( InterruptedException e )
                {
                    // TODO
                }
            }
        }

        if ( !errors.isEmpty() )
        {
            throw new DatabaseException( "Failed to store %d dependency relationships.", errors,
                                         errors.size() );
        }

    }

    public void storeDependencies( final FullProjectKey projectKey,
                                   final List<Dependency> dependencies,
                                   final DependencyDBSession session )
        throws DatabaseException
    {
        Set<DependencyRelationship> rels = new HashSet<DependencyRelationship>();
        for ( Dependency dep : dependencies )
        {
            rels.add( new DependencyRelationship( dep, projectKey ) );
        }

        store( rels, session );
    }

    public void store( final DependencyRelationship rel, final DependencyDBSession session )
        throws DatabaseException
    {
        if ( hasDirectDependency( rel, session ) )
        {
            return;
        }

        HttpPost request = new HttpPost( session.getBaseUrl() );
        try
        {
            request.setEntity( new StringEntity( serializer.toString( rel ), "application/json",
                                                 "UTF-8" ) );

            HttpResponse response = getClient().execute( request );
            StatusLine statusLine = response.getStatusLine();
            if ( statusLine.getStatusCode() != HttpStatus.SC_CREATED )
            {
                throw new DatabaseException(
                                             "Failed to store dependency relationship: %s.\nHTTP Response: %s",
                                             rel, statusLine );
            }
        }
        catch ( UnsupportedEncodingException e )
        {
            throw new DatabaseException(
                                         "Failed to set HTTP entity for POST to store dependency relationship: %s.\nReason: %s",
                                         e, rel, e.getMessage() );
        }
        catch ( ClientProtocolException e )
        {
            throw new DatabaseException(
                                         "Failed to store dependency relationship: %s.\nReason: %s",
                                         e, rel, e.getMessage() );
        }
        catch ( IOException e )
        {
            throw new DatabaseException(
                                         "Failed to store dependency relationship: %s.\nReason: %s",
                                         e, rel, e.getMessage() );
        }
        finally
        {
            cleanup( request );
        }
    }

    public void deleteDependencies( final FullProjectKey projectKey,
                                    final List<Dependency> dependencies,
                                    final DependencyDBSession session )
        throws DatabaseException
    {
        Set<DependencyRelationship> rels = new HashSet<DependencyRelationship>();
        for ( Dependency dep : dependencies )
        {
            rels.add( new DependencyRelationship( dep, projectKey ) );
        }

        CountDownLatch latch = new CountDownLatch( rels.size() );
        List<DatabaseException> errors = new ArrayList<DatabaseException>();

        for ( DependencyRelationship rel : rels )
        {
            exec.execute( new DeletionWorker( this, latch, rel, session, errors ) );
        }

        synchronized ( latch )
        {
            while ( latch.getCount() > 0 )
            {
                LOGGER.info( "Waiting for " + latch.getCount() + " workers." );
                try
                {
                    latch.await( 3, TimeUnit.SECONDS );
                }
                catch ( InterruptedException e )
                {
                    // TODO
                }
            }
        }

        if ( !errors.isEmpty() )
        {
            throw new DatabaseException( "Failed to delete %d dependency relationships.", errors,
                                         errors.size() );
        }
    }

    public void delete( final DependencyRelationship rel, final DependencyDBSession session )
        throws DatabaseException
    {
        if ( !hasDirectDependency( rel, session ) )
        {
            return;
        }

        String url = buildDocUrl( rel, true, session );
        HttpDelete request = new HttpDelete( url );
        try
        {
            HttpResponse response = getClient().execute( request );
            StatusLine statusLine = response.getStatusLine();
            if ( statusLine.getStatusCode() != HttpStatus.SC_OK )
            {
                throw new DatabaseException(
                                             "Failed to delete dependency relationship: %s.\nHTTP Response: %s\nURL: %s",
                                             rel, statusLine, url );
            }
        }
        catch ( ClientProtocolException e )
        {
            throw new DatabaseException(
                                         "Failed to delete dependency relationship: %s.\nReason: %s\nURL: %s",
                                         e, rel, e.getMessage(), url );
        }
        catch ( IOException e )
        {
            throw new DatabaseException(
                                         "Failed to delete dependency relationship: %s.\nReason: %s\nURL: %s",
                                         e, rel, e.getMessage(), url );
        }
        finally
        {
            cleanup( request );
        }
    }

    public boolean hasDirectDependency( final FullProjectKey dependent,
                                        final FullProjectKey dependency,
                                        final DependencyDBSession session )
        throws DatabaseException
    {
        return hasDirectDependency( new DependencyRelationship( dependent, dependency ), session );
    }

    public boolean hasDirectDependency( final DependencyRelationship rel,
                                        final DependencyDBSession session )
        throws DatabaseException
    {
        String url = buildDocUrl( rel, false, session );

        boolean exists = false;

        HttpHead request = new HttpHead( url );
        try
        {
            HttpResponse response = getClient().execute( request );
            StatusLine statusLine = response.getStatusLine();
            exists = statusLine.getStatusCode() == HttpStatus.SC_OK;

            if ( exists )
            {
                Header etag = response.getFirstHeader( "Etag" );
                String rev = etag.getValue();
                if ( rev.startsWith( "\"" ) || rev.startsWith( "'" ) )
                {
                    rev = rev.substring( 1 );
                }

                if ( rev.endsWith( "\"" ) || rev.endsWith( "'" ) )
                {
                    rev = rev.substring( 0, rev.length() - 1 );
                }

                rel.setRev( rev );
            }
        }
        catch ( ClientProtocolException e )
        {
            throw new DatabaseException(
                                         "Failed to check for existence of dependency relationship: %s.\nReason: %s",
                                         e, rel, e.getMessage() );
        }
        catch ( IOException e )
        {
            throw new DatabaseException(
                                         "Failed to check for existence of dependency relationship: %s.\nReason: %s",
                                         e, rel, e.getMessage() );
        }
        finally
        {
            cleanup( request );
        }

        return exists;
    }

    protected DependencyRelationshipListing getDependencyRelationshipView( final String viewName,
                                                                           final FullProjectKey projectKey,
                                                                           final DependencyDBSession session )
        throws DatabaseException
    {
        Map<String, String> params = new HashMap<String, String>();
        params.put( KEY, stringParam( projectKey ) );
        params.put( INCLUDE_DOCS, "true" );

        String url;
        try
        {
            url =
                buildUrl( session, params, "_design", session.getLogicApplication(), "_view",
                          viewName );
        }
        catch ( MalformedURLException e )
        {
            throw new DatabaseException(
                                         "Failed to build URL for view: %s against project: %s.\nReason: %s",
                                         e, viewName, projectKey, e.getMessage() );
        }

        if ( LOGGER.isDebugEnabled() )
        {
            LOGGER.debug( "GET: " + url );
        }

        HttpGet request = new HttpGet( url );
        DependencyRelationshipListingHandler handler =
            new DependencyRelationshipListingHandler( serializer );

        try
        {
            DependencyRelationshipListing listing = client.execute( request, handler );
            if ( listing == null && handler.getError() != null )
            {
                throw handler.getError();
            }
            else
            {
                return listing;
            }
        }
        catch ( ClientProtocolException e )
        {
            throw new DatabaseException(
                                         "Failed to read listing for view: %s against project: %s.\nReason: %s",
                                         e, viewName, projectKey, e.getMessage() );
        }
        catch ( IOException e )
        {
            throw new DatabaseException(
                                         "Failed to read listing for view: %s against project: %s.\nReason: %s",
                                         e, viewName, projectKey, e.getMessage() );
        }
        finally
        {
            cleanup( request );
        }
    }

    protected synchronized HttpClient getClient()
    {
        if ( client == null )
        {
            ThreadSafeClientConnManager ccm = new ThreadSafeClientConnManager();
            ccm.setMaxTotal( 20 );

            client = new DefaultHttpClient( ccm );
        }
        return client;
    }

    protected String buildDocUrl( final DependencyRelationship rel, final boolean includeRevision,
                                  final DependencyDBSession session )
        throws DatabaseException
    {
        try
        {
            String url;
            if ( includeRevision && rel.getRev() != null )
            {
                Map<String, String> params = Collections.singletonMap( REV, rel.getRev() );
                url = buildUrl( session, params, rel.getId() );
            }
            else
            {
                url = buildUrl( session, null, rel.getId() );
            }

            return url;
        }
        catch ( MalformedURLException e )
        {
            throw new DatabaseException(
                                         "Failed to format document URL for id: %s [revision=%s].\nReason: %s",
                                         e, rel.getId(), rel.getRev(), e.getMessage() );
        }
    }

    protected String buildUrl( final DependencyDBSession session, final Map<String, String> params,
                               final String... parts )
        throws MalformedURLException
    {
        StringBuilder urlBuilder = new StringBuilder( session.getBaseUrl() );
        for ( String part : parts )
        {
            if ( part.startsWith( "/" ) )
            {
                part = part.substring( 1 );
            }

            if ( urlBuilder.charAt( urlBuilder.length() - 1 ) != '/' )
            {
                urlBuilder.append( "/" );
            }

            urlBuilder.append( part );
        }

        if ( params != null && !params.isEmpty() )
        {
            urlBuilder.append( "?" );
            boolean first = true;
            for ( Map.Entry<String, String> param : params.entrySet() )
            {
                if ( first )
                {
                    first = false;
                }
                else
                {
                    urlBuilder.append( "&" );
                }

                urlBuilder.append( param.getKey() ).append( "=" ).append( param.getValue() );
            }
        }

        return new URL( urlBuilder.toString() ).toExternalForm();
    }

    protected String stringParam( final Object value )
    {
        return "%22" + String.valueOf( value ) + "%22";
    }

    private void cleanup( final HttpRequestBase request )
    {
        request.abort();
        getClient().getConnectionManager().closeExpiredConnections();
        getClient().getConnectionManager().closeIdleConnections( 2, TimeUnit.SECONDS );
    }

    private static final class StorageWorker
        implements Runnable
    {
        private final DependencyDatabase db;

        private final CountDownLatch latch;

        private final DependencyRelationship rel;

        private final DependencyDBSession session;

        private final List<DatabaseException> errors;

        StorageWorker( final DependencyDatabase db, final CountDownLatch latch,
                       final DependencyRelationship rel, final DependencyDBSession session,
                       final List<DatabaseException> errors )
        {
            this.db = db;
            this.latch = latch;
            this.rel = rel;
            this.session = session;
            this.errors = errors;
        }

        @Override
        public void run()
        {
            try
            {
                db.store( rel, session );
            }
            catch ( DatabaseException e )
            {
                synchronized ( errors )
                {
                    errors.add( e );
                }
            }
            finally
            {
                latch.countDown();
            }
        }

    }

    private static final class DeletionWorker
        implements Runnable
    {
        private final DependencyDatabase db;

        private final CountDownLatch latch;

        private final DependencyRelationship rel;

        private final DependencyDBSession session;

        private final List<DatabaseException> errors;

        DeletionWorker( final DependencyDatabase db, final CountDownLatch latch,
                        final DependencyRelationship rel, final DependencyDBSession session,
                        final List<DatabaseException> errors )
        {
            this.db = db;
            this.latch = latch;
            this.rel = rel;
            this.session = session;
            this.errors = errors;
        }

        @Override
        public void run()
        {
            try
            {
                db.delete( rel, session );
            }
            catch ( DatabaseException e )
            {
                synchronized ( errors )
                {
                    errors.add( e );
                }
            }
            finally
            {
                latch.countDown();
            }
        }

    }

}
