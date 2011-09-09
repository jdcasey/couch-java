package org.commonjava.web.user.rest;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.lang.reflect.Type;

import javax.inject.Inject;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.commonjava.auth.couch.conf.UserManagerConfiguration;
import org.commonjava.auth.couch.data.UserDataException;
import org.commonjava.auth.couch.data.UserDataManager;
import org.commonjava.couch.db.CouchManager;
import org.commonjava.web.common.ser.RestSerializer;
import org.commonjava.web.user.rest.fixture.TestProducers;
import org.commonjava.web.user.rest.fixture.TestRESTApplication;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.asset.UrlAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Before;

public abstract class AbstractRESTUserManagerTest
{
    @Inject
    protected RestSerializer serializer;

    @Inject
    protected UserDataManager dataManager;

    @Inject
    protected UserManagerConfiguration config;

    @Inject
    protected CouchManager couch;

    protected HttpClient http;

    protected AbstractRESTUserManagerTest()
    {}

    @Deployment
    public static Archive<?> createTestArchive()
    {
        WebArchive war = ShrinkWrap.create( WebArchive.class, "test.war" );

        war.addPackages( true, "org.commonjava" );
        war.addPackages( true, "org.apache.http" );
        war.addPackages( true, "org.apache.shiro" );
        war.addPackages( true, "org.apache.commons.lang" );
        war.addPackages( true, "org.apache.commons.codec" );
        war.addPackages( true, "org.apache.commons.io" );
        war.addPackages( true, "org.apache.log4j" );
        war.addPackages( true, "com.google.gson" );
        war.addPackages( true, "org.slf4j" );
        war.addPackages( true, "org.apache.commons.logging" );

        war.addClass( TestProducers.class );
        war.addClass( TestRESTApplication.class );

        war.addAsWebInfResource( EmptyAsset.INSTANCE, "beans.xml" );

        war.addAsWebInfResource( new UrlAsset(
                                               Thread.currentThread().getContextClassLoader().getResource( "log4j.properties" ) ),
                                 "classes/log4j.properties" );

        return war;
    }

    @Before
    public void setupFixtures()
        throws UserDataException
    {
        http = new DefaultHttpClient();
        dataManager.install();
        dataManager.setupAdminInformation();
    }

    @After
    public void teardownFixtures()
        throws Exception
    {
        couch.dropDatabase( config.getDatabaseUrl() );
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
        request.setEntity( new StringEntity( serializer.toJson( value ), "application/json",
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
        request.setEntity( new StringEntity( serializer.toJson( value, type ), "application/json",
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
}