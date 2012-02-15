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
package org.commonjava.couch.test.fixture;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.commonjava.web.json.model.Listing;
import org.commonjava.web.json.ser.JsonSerializer;
import org.junit.rules.ExternalResource;

import com.google.gson.reflect.TypeToken;

public class RemoteRESTFixture
    extends ExternalResource
{
    protected static final String HOST = "localhost";

    protected static final int PORT = 8080;

    protected DefaultHttpClient http;

    private final JsonSerializer serializer;

    private final int port;

    private final String host;

    public RemoteRESTFixture( final String host, final int port, final JsonSerializer serializer )
    {
        this.host = host;
        this.port = port;
        this.serializer = serializer;
    }

    public void assertLocationHeader( final HttpResponse response, final String value )
    {
        final Header[] headers = response.getHeaders( "Location" );
        assertThat( headers, notNullValue() );
        assertThat( headers.length, equalTo( 1 ) );

        final String header = headers[0].getValue();
        assertThat( header, equalTo( value ) );
    }

    public <T> T get( final String url, final Class<T> type )
        throws Exception
    {
        final HttpGet get = new HttpGet( fixup( url ) );
        try
        {
            return http.execute( get, new ResponseHandler<T>()
            {
                @SuppressWarnings( "unchecked" )
                @Override
                public T handleResponse( final HttpResponse response )
                    throws ClientProtocolException, IOException
                {
                    final StatusLine sl = response.getStatusLine();
                    assertThat( sl.getStatusCode(), equalTo( HttpStatus.SC_OK ) );

                    return serializer.fromStream( response.getEntity()
                                                          .getContent(), "UTF-8", type );
                }
            } );
        }
        finally
        {
            get.abort();
        }
    }

    public void get( final String url, final int expectedStatus )
        throws Exception
    {
        final HttpGet get = new HttpGet( fixup( url ) );
        try
        {
            http.execute( get, new ResponseHandler<Void>()
            {
                @Override
                public Void handleResponse( final HttpResponse response )
                    throws ClientProtocolException, IOException
                {
                    final StatusLine sl = response.getStatusLine();
                    assertThat( sl.getStatusCode(), equalTo( expectedStatus ) );

                    return null;
                }
            } );
        }
        finally
        {
            get.abort();
        }
    }

    public <T> Listing<T> getListing( final String url, final TypeToken<Listing<T>> token )
        throws Exception
    {
        final HttpGet get = new HttpGet( fixup( url ) );
        try
        {
            return http.execute( get, new ResponseHandler<Listing<T>>()
            {
                @SuppressWarnings( "unchecked" )
                @Override
                public Listing<T> handleResponse( final HttpResponse response )
                    throws ClientProtocolException, IOException
                {
                    final StatusLine sl = response.getStatusLine();
                    assertThat( sl.getStatusCode(), equalTo( HttpStatus.SC_OK ) );

                    return serializer.listingFromStream( response.getEntity()
                                                                 .getContent(), "UTF-8", token );
                }
            } );
        }
        finally
        {
            get.abort();
        }
    }

    public HttpResponse delete( final String url )
        throws Exception
    {
        final HttpDelete request = new HttpDelete( fixup( url ) );
        try
        {
            final HttpResponse response = http.execute( request );

            assertThat( response.getStatusLine()
                                .getStatusCode(), equalTo( HttpStatus.SC_OK ) );

            return response;
        }
        finally
        {
            request.abort();
        }
    }

    public HttpResponse post( final String url, final Object value, final int status )
        throws Exception
    {
        final HttpPost request = new HttpPost( fixup( url ) );
        request.setEntity( new StringEntity( serializer.toString( value ), "application/json", "UTF-8" ) );

        try
        {
            final HttpResponse response = http.execute( request );

            assertThat( response.getStatusLine()
                                .getStatusCode(), equalTo( status ) );

            return response;
        }
        finally
        {
            request.abort();
        }
    }

    public HttpResponse post( final String url, final Object value, final Type type, final int status )
        throws Exception
    {
        final HttpPost request = new HttpPost( fixup( url ) );
        request.setEntity( new StringEntity( serializer.toString( value, type ), "application/json", "UTF-8" ) );

        try
        {
            final HttpResponse response = http.execute( request );

            assertThat( response.getStatusLine()
                                .getStatusCode(), equalTo( status ) );

            return response;
        }
        finally
        {
            request.abort();
        }
    }

    private URI fixup( String url )
        throws URISyntaxException
    {
        if ( !url.startsWith( "http://" ) )
        {
            url = "http://" + host + ":" + port + "/" + url;
        }

        return new URI( url );
    }

    @Override
    protected void before()
        throws Throwable
    {
        super.before();

        final ThreadSafeClientConnManager ccm = new ThreadSafeClientConnManager();
        ccm.setMaxTotal( 20 );

        http = new DefaultHttpClient( ccm );
    }
}
