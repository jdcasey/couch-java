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
package org.commonjava.web.test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.lang.reflect.Type;

import javax.inject.Inject;

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
import org.commonjava.couch.db.CouchManager;
import org.commonjava.web.common.model.Listing;
import org.commonjava.web.common.ser.JsonSerializer;
import org.junit.Before;

import com.google.gson.reflect.TypeToken;

public abstract class AbstractRESTCouchTest
{
    protected static final String HOST = "localhost";

    protected static final int PORT = 8080;

    @Inject
    protected JsonSerializer serializer;

    protected DefaultHttpClient http;

    protected AbstractRESTCouchTest()
    {
    }

    protected abstract CouchManager getCouchManager();

    @Before
    public final void setupRESTCouchTest()
        throws Exception
    {
        getCouchManager().dropDatabase();

        final ThreadSafeClientConnManager ccm = new ThreadSafeClientConnManager();
        ccm.setMaxTotal( 20 );

        http = new DefaultHttpClient( ccm );
    }

    // @After
    public final void teardownRESTCouchTest()
        throws Exception
    {
        getCouchManager().dropDatabase();
    }

    protected void assertLocationHeader( final HttpResponse response, final String value )
    {
        final Header[] headers = response.getHeaders( "Location" );
        assertThat( headers, notNullValue() );
        assertThat( headers.length, equalTo( 1 ) );

        final String header = headers[0].getValue();
        assertThat( header, equalTo( value ) );
    }

    protected <T> T get( final String url, final Class<T> type )
        throws Exception
    {
        final HttpGet get = new HttpGet( url );
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

    protected void get( final String url, final int expectedStatus )
        throws Exception
    {
        final HttpGet get = new HttpGet( url );
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

    protected <T> Listing<T> getListing( final String url, final TypeToken<Listing<T>> token )
        throws Exception
    {
        final HttpGet get = new HttpGet( url );
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

    protected HttpResponse delete( final String url )
        throws Exception
    {
        final HttpDelete request = new HttpDelete( url );
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

    protected HttpResponse post( final String url, final Object value, final int status )
        throws Exception
    {
        final HttpPost request = new HttpPost( url );
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

    protected HttpResponse post( final String url, final Object value, final Type type, final int status )
        throws Exception
    {
        final HttpPost request = new HttpPost( url );
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
}
