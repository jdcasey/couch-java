/*******************************************************************************
 * Copyright (C) 2011  John Casey
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public
 * License along with this program.  If not, see 
 * <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package org.commonjava.couch.io;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;

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
import org.commonjava.couch.io.json.SerializationAdapter;
import org.commonjava.couch.model.CouchError;

@Singleton
public class CouchHttpClient
{
    private HttpClient client;

    @Inject
    private CouchDBConfiguration config;

    private final Serializer serializer;

    public CouchHttpClient()
    {
        this.serializer = new Serializer();
    }

    public CouchHttpClient( final CouchDBConfiguration config, final Serializer serializer )
    {
        this.config = config;
        this.serializer = serializer;
        setupClient();
    }

    @PostConstruct
    private void setupClient()
    {
        ThreadSafeClientConnManager ccm = new ThreadSafeClientConnManager();
        ccm.setMaxTotal( config.getMaxConnections() );

        DefaultHttpClient c = new DefaultHttpClient( ccm );

        if ( config.getDatabaseUser() != null )
        {
            AuthScope scope = new AuthScope( config.getDatabaseHost(), config.getDatabasePort() );
            UsernamePasswordCredentials cred =
                new UsernamePasswordCredentials( config.getDatabaseUser(),
                                                 config.getDatabasePassword() );

            c.getCredentialsProvider().setCredentials( scope, cred );
        }

        client = c;
    }

    public void executeHttp( final HttpRequestBase request, final String failureMessage )
        throws CouchDBException
    {
        executeHttp( request, null, failureMessage );
    }

    public void executeHttp( final HttpRequestBase request, final Integer expectedStatus,
                             final Object failureMessage )
        throws CouchDBException
    {
        String url = request.getURI().toString();

        try
        {
            HttpResponse response = client.execute( request );
            StatusLine statusLine = response.getStatusLine();
            if ( expectedStatus != null && statusLine.getStatusCode() != expectedStatus )
            {
                HttpEntity entity = response.getEntity();
                CouchError error = serializer.toError( entity );
                throw new CouchDBException( "%s: %s.\nHTTP Response: %s\nError: %s",
                                            failureMessage, url, statusLine, error );
            }
        }
        catch ( UnsupportedEncodingException e )
        {
            throw new CouchDBException( "%s: %s.\nReason: %s", e, failureMessage, url,
                                        e.getMessage() );
        }
        catch ( ClientProtocolException e )
        {
            throw new CouchDBException( "%s: %s.\nReason: %s", e, failureMessage, url,
                                        e.getMessage() );
        }
        catch ( IOException e )
        {
            throw new CouchDBException( "%s: %s.\nReason: %s", e, failureMessage, url,
                                        e.getMessage() );
        }
        finally
        {
            cleanup( request );
        }
    }

    public HttpResponse executeHttpWithResponse( final HttpRequestBase request,
                                                 final String failureMessage )
        throws CouchDBException
    {
        return executeHttpWithResponse( request, null, failureMessage );
    }

    public HttpResponse executeHttpWithResponse( final HttpRequestBase request,
                                                 final Integer expectedStatus,
                                                 final Object failureMessage )
        throws CouchDBException
    {
        String url = request.getURI().toString();

        boolean failed = false;
        try
        {
            HttpResponse response = client.execute( request );
            StatusLine statusLine = response.getStatusLine();
            if ( expectedStatus != null && statusLine.getStatusCode() != expectedStatus )
            {
                HttpEntity entity = response.getEntity();
                CouchError error = serializer.toError( entity );
                throw new CouchDBException( "%s: %s.\nHTTP Response: %s\nError: %s",
                                            failureMessage, url, statusLine, error );
            }

            return response;
        }
        catch ( UnsupportedEncodingException e )
        {
            failed = true;
            throw new CouchDBException( "%s: %s.\nReason: %s", e, failureMessage, url,
                                        e.getMessage() );
        }
        catch ( ClientProtocolException e )
        {
            failed = true;
            throw new CouchDBException( "%s: %s.\nReason: %s", e, failureMessage, url,
                                        e.getMessage() );
        }
        catch ( IOException e )
        {
            failed = true;
            throw new CouchDBException( "%s: %s.\nReason: %s", e, failureMessage, url,
                                        e.getMessage() );
        }
        finally
        {
            if ( failed )
            {
                cleanup( request );
            }
        }
    }

    public <T> T executeHttpAndReturn( final HttpRequestBase request, final Type type,
                                       final Object failureMessage,
                                       final SerializationAdapter... adapters )
        throws CouchDBException
    {
        return executeHttpAndReturn( request, new SerializedGetHandler<T>( serializer, type,
                                                                           adapters ),
                                     failureMessage );
    }

    public <T> T executeHttpAndReturn( final HttpRequestBase request,
                                       final ResponseHandlerWithError<T> handler,
                                       final Object failureMessage )
        throws CouchDBException
    {
        String url = request.getURI().toString();

        try
        {
            T result = client.execute( request, handler );
            if ( result == null && handler.getError() != null )
            {
                throw handler.getError();
            }

            return result;
        }
        catch ( UnsupportedEncodingException e )
        {
            throw new CouchDBException( "%s: %s.\nReason: %s", e, failureMessage, url,
                                        e.getMessage() );
        }
        catch ( ClientProtocolException e )
        {
            throw new CouchDBException( "%s: %s.\nReason: %s", e, failureMessage, url,
                                        e.getMessage() );
        }
        catch ( IOException e )
        {
            throw new CouchDBException( "%s: %s.\nReason: %s", e, failureMessage, url,
                                        e.getMessage() );
        }
        finally
        {
            cleanup( request );
        }
    }

    public void cleanup( final HttpRequestBase request )
    {
        request.abort();
        client.getConnectionManager().closeExpiredConnections();
        client.getConnectionManager().closeIdleConnections( 2, TimeUnit.SECONDS );
    }
}
