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
package org.commonjava.couch.io;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Alternative;
import javax.inject.Named;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.commonjava.couch.conf.CouchDBConfiguration;
import org.commonjava.couch.db.CouchDBException;
import org.commonjava.couch.db.handler.ResponseHandlerWithError;
import org.commonjava.couch.db.handler.SerializedGetHandler;
import org.commonjava.couch.model.CouchError;
import org.commonjava.web.json.ser.WebSerializationAdapter;

@Named( "dont-use-directly" )
@Alternative
public class CouchHttpClient
{
    private HttpClient client;

    private final CouchDBConfiguration config;

    private final Serializer serializer;

    public CouchHttpClient( final CouchDBConfiguration config, final Serializer serializer )
    {
        this.config = config;
        this.serializer = serializer;
        setupClient();
    }

    @PostConstruct
    private void setupClient()
    {
        final ThreadSafeClientConnManager ccm = new ThreadSafeClientConnManager();
        ccm.setMaxTotal( config.getMaxConnections() );

        final DefaultHttpClient c = new DefaultHttpClient( ccm );

        if ( config.getDatabaseUser() != null )
        {
            final AuthScope scope = new AuthScope( config.getDatabaseHost(), config.getDatabasePort() );
            final UsernamePasswordCredentials cred =
                new UsernamePasswordCredentials( config.getDatabaseUser(), config.getDatabasePassword() );

            c.getCredentialsProvider()
             .setCredentials( scope, cred );
        }

        client = c;
    }

    public void executeHttp( final HttpRequestBase request, final String failureMessage )
        throws CouchDBException
    {
        executeHttp( request, null, failureMessage );
    }

    public void executeHttp( final HttpRequestBase request, final Integer expectedStatus, final Object failureMessage )
        throws CouchDBException
    {
        final String url = request.getURI()
                                  .toString();

        try
        {
            final HttpResponse response = client.execute( request );
            final StatusLine statusLine = response.getStatusLine();
            if ( expectedStatus != null && statusLine.getStatusCode() != expectedStatus )
            {
                final HttpEntity entity = response.getEntity();
                final CouchError error = serializer.toError( entity );
                throw new CouchDBException( "%s: %s.\nHTTP Response: %s\nError: %s", failureMessage, url, statusLine,
                                            error );
            }
        }
        catch ( final UnsupportedEncodingException e )
        {
            throw new CouchDBException( "%s: %s.\nReason: %s", e, failureMessage, url, e.getMessage() );
        }
        catch ( final ClientProtocolException e )
        {
            throw new CouchDBException( "%s: %s.\nReason: %s", e, failureMessage, url, e.getMessage() );
        }
        catch ( final IOException e )
        {
            throw new CouchDBException( "%s: %s.\nReason: %s", e, failureMessage, url, e.getMessage() );
        }
        finally
        {
            cleanup( request );
        }
    }

    public HttpResponse executeHttpWithResponse( final HttpRequestBase request, final String failureMessage )
        throws CouchDBException
    {
        return executeHttpWithResponse( request, null, failureMessage );
    }

    public HttpResponse executeHttpWithResponse( final HttpRequestBase request, final Integer expectedStatus,
                                                 final Object failureMessage )
        throws CouchDBException
    {
        final String url = request.getURI()
                                  .toString();

        boolean failed = false;
        try
        {
            final HttpResponse response = client.execute( request );
            final StatusLine statusLine = response.getStatusLine();
            if ( expectedStatus != null && statusLine.getStatusCode() != expectedStatus )
            {
                final HttpEntity entity = response.getEntity();
                final CouchError error = serializer.toError( entity );
                throw new CouchDBException( "%s: %s.\nHTTP Response: %s\nError: %s", failureMessage, url, statusLine,
                                            error );
            }

            return response;
        }
        catch ( final UnsupportedEncodingException e )
        {
            failed = true;
            throw new CouchDBException( "%s: %s.\nReason: %s", e, failureMessage, url, e.getMessage() );
        }
        catch ( final ClientProtocolException e )
        {
            failed = true;
            throw new CouchDBException( "%s: %s.\nReason: %s", e, failureMessage, url, e.getMessage() );
        }
        catch ( final IOException e )
        {
            failed = true;
            throw new CouchDBException( "%s: %s.\nReason: %s", e, failureMessage, url, e.getMessage() );
        }
        finally
        {
            if ( failed )
            {
                cleanup( request );
            }
        }
    }

    public <T> T executeHttpAndReturn( final HttpRequestBase request, final Type type, final Object failureMessage,
                                       final WebSerializationAdapter... adapters )
        throws CouchDBException
    {
        return executeHttpAndReturn( request, new SerializedGetHandler<T>( serializer, type, adapters ), failureMessage );
    }

    public <T> T executeHttpAndReturn( final HttpRequestBase request, final ResponseHandlerWithError<T> handler,
                                       final Object failureMessage )
        throws CouchDBException
    {
        final String url = request.getURI()
                                  .toString();

        try
        {
            final T result = client.execute( request, handler );
            if ( result == null && handler.getError() != null )
            {
                throw handler.getError();
            }

            return result;
        }
        catch ( final UnsupportedEncodingException e )
        {
            throw new CouchDBException( "%s: %s.\nReason: %s", e, failureMessage, url, e.getMessage() );
        }
        catch ( final ClientProtocolException e )
        {
            throw new CouchDBException( "%s: %s.\nReason: %s", e, failureMessage, url, e.getMessage() );
        }
        catch ( final IOException e )
        {
            throw new CouchDBException( "%s: %s.\nReason: %s", e, failureMessage, url, e.getMessage() );
        }
        finally
        {
            cleanup( request );
        }
    }

    public void cleanup( final HttpRequestBase request )
    {
        request.abort();
        client.getConnectionManager()
              .closeExpiredConnections();
        client.getConnectionManager()
              .closeIdleConnections( 2, TimeUnit.SECONDS );
    }
}
