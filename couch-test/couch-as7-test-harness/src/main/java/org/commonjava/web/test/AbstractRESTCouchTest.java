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

import java.lang.reflect.Type;
import java.net.MalformedURLException;

import javax.inject.Inject;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.commonjava.couch.db.CouchManager;
import org.commonjava.web.common.model.Listing;
import org.commonjava.web.common.ser.JsonSerializer;
import org.commonjava.web.test.fixture.WebFixture;
import org.junit.Before;
import org.junit.Rule;

import com.google.gson.reflect.TypeToken;

public abstract class AbstractRESTCouchTest
{
    protected static final String HOST = "localhost";

    protected static final int PORT = 8080;

    @Inject
    protected JsonSerializer serializer;

    @Rule
    public WebFixture http;

    protected void disableRedirection()
    {
        http.disableRedirection();
    }

    protected void enableRedirection()
    {
        http.enableRedirection();
    }

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
        return http.get( url, type );
    }

    protected void get( final String url, final int expectedStatus )
        throws Exception
    {
        http.get( url, expectedStatus );
    }

    protected HttpResponse getWithResponse( final String url, final int expectedStatus )
        throws Exception
    {
        return http.getWithResponse( url, expectedStatus );
    }

    protected <T> Listing<T> getListing( final String url, final TypeToken<Listing<T>> token )
        throws Exception
    {
        return http.getListing( url, token );
    }

    protected HttpResponse delete( final String url )
        throws Exception
    {
        return http.delete( url );
    }

    protected HttpResponse post( final String url, final Object value, final int status )
        throws Exception
    {
        return http.post( url, value, status );
    }

    protected HttpResponse post( final String url, final Object value, final Type type, final int status )
        throws Exception
    {
        return http.post( url, value, type, status );
    }

    protected String resourceUrl( final String path )
        throws MalformedURLException
    {
        return http.resourceUrl( path );
    }

    protected String apiVersion()
    {
        return "1.0";
    }

}
