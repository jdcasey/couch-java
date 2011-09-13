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
import org.commonjava.auth.couch.conf.UserManagerConfiguration;
import org.commonjava.auth.couch.data.PasswordManager;
import org.commonjava.auth.couch.data.UserDataException;
import org.commonjava.auth.couch.data.UserDataManager;
import org.commonjava.auth.shiro.couch.CouchRealm;
import org.commonjava.couch.db.CouchManager;
import org.commonjava.web.common.model.Listing;
import org.commonjava.web.common.ser.JsonSerializer;
import org.junit.After;
import org.junit.Before;

import com.google.gson.reflect.TypeToken;

public abstract class AbstractRESTCouchTest
{
    protected static final String HOST = "localhost";

    protected static final int PORT = 8080;

    @Inject
    protected JsonSerializer serializer;

    @Inject
    protected UserDataManager dataManager;

    @Inject
    protected UserManagerConfiguration config;

    @Inject
    protected CouchManager couch;

    @Inject
    protected PasswordManager passwordManager;

    @Inject
    protected CouchRealm realm;

    protected DefaultHttpClient http;

    protected AbstractRESTCouchTest()
    {}

    @Before
    public void setupFixtures()
        throws UserDataException
    {
        dataManager.install();
        dataManager.setupAdminInformation();

        // setup the security manager.
        realm.setupSecurityManager();

        ThreadSafeClientConnManager ccm = new ThreadSafeClientConnManager();
        ccm.setMaxTotal( 20 );

        http = new DefaultHttpClient( ccm );
    }

    @After
    public void teardownFixtures()
        throws Exception
    {
        couch.dropDatabase();
    }

    protected void assertLocationHeader( final HttpResponse response, final String value )
    {
        Header[] headers = response.getHeaders( "Location" );
        assertThat( headers, notNullValue() );
        assertThat( headers.length, equalTo( 1 ) );

        String header = headers[0].getValue();
        assertThat( header, equalTo( value ) );
    }

    protected <T> T get( final String url, final Class<T> type )
        throws Exception
    {
        HttpGet get = new HttpGet( url );
        try
        {
            return http.execute( get, new ResponseHandler<T>()
            {
                @SuppressWarnings( "unchecked" )
                @Override
                public T handleResponse( final HttpResponse response )
                    throws ClientProtocolException, IOException
                {
                    StatusLine sl = response.getStatusLine();
                    assertThat( sl.getStatusCode(), equalTo( HttpStatus.SC_OK ) );

                    return serializer.fromStream( response.getEntity().getContent(), "UTF-8", type );
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
        HttpGet get = new HttpGet( url );
        try
        {
            http.execute( get, new ResponseHandler<Void>()
            {
                @Override
                public Void handleResponse( final HttpResponse response )
                    throws ClientProtocolException, IOException
                {
                    StatusLine sl = response.getStatusLine();
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
        HttpGet get = new HttpGet( url );
        try
        {
            return http.execute( get, new ResponseHandler<Listing<T>>()
            {
                @SuppressWarnings( "unchecked" )
                @Override
                public Listing<T> handleResponse( final HttpResponse response )
                    throws ClientProtocolException, IOException
                {
                    StatusLine sl = response.getStatusLine();
                    assertThat( sl.getStatusCode(), equalTo( HttpStatus.SC_OK ) );

                    return serializer.listingFromStream( response.getEntity().getContent(),
                                                         "UTF-8", token );
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
        HttpDelete request = new HttpDelete( url );
        try
        {
            HttpResponse response = http.execute( request );

            assertThat( response.getStatusLine().getStatusCode(), equalTo( HttpStatus.SC_OK ) );

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
        HttpPost request = new HttpPost( url );
        request.setEntity( new StringEntity( serializer.toString( value ), "application/json",
                                             "UTF-8" ) );

        try
        {
            HttpResponse response = http.execute( request );

            assertThat( response.getStatusLine().getStatusCode(), equalTo( status ) );

            return response;
        }
        finally
        {
            request.abort();
        }
    }

    protected HttpResponse post( final String url, final Object value, final Type type,
                                 final int status )
        throws Exception
    {
        HttpPost request = new HttpPost( url );
        request.setEntity( new StringEntity( serializer.toString( value, type ),
                                             "application/json", "UTF-8" ) );

        try
        {
            HttpResponse response = http.execute( request );

            assertThat( response.getStatusLine().getStatusCode(), equalTo( status ) );

            return response;
        }
        finally
        {
            request.abort();
        }
    }
}