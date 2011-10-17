package org.commonjava.couch.fixture;

import java.util.Arrays;
import java.util.concurrent.Executors;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.commonjava.couch.change.CouchChangeListener;
import org.commonjava.couch.change.dispatch.CouchChangeDispatcher;
import org.commonjava.couch.change.dispatch.ThreadableListener;
import org.commonjava.couch.change.dispatch.ThreadedChangeDispatcher;
import org.commonjava.couch.conf.CouchDBConfiguration;
import org.commonjava.couch.conf.DefaultCouchDBConfiguration;
import org.commonjava.couch.db.CouchDBException;
import org.commonjava.couch.db.CouchManager;
import org.commonjava.couch.io.CouchAppReader;
import org.commonjava.couch.io.CouchHttpClient;
import org.commonjava.couch.io.Serializer;
import org.jboss.weld.environment.se.WeldContainer;
import org.junit.rules.ExternalResource;

public class DBFixture
    extends ExternalResource
{

    public static final String DB_URL = "http://localhost:5984/test-db";

    private final CouchDBConfiguration couchConfig;

    private final Serializer serializer;

    private final CouchHttpClient couchHttp;

    private final CouchManager couchManager;

    private final CouchChangeListener listener;

    public DBFixture()
    {
        this( (CouchChangeDispatcher) null );
    }

    public DBFixture( final CouchChangeDispatcher dispatcher )
    {
        couchConfig = new DefaultCouchDBConfiguration( DB_URL );

        serializer = new Serializer();

        couchHttp = new CouchHttpClient( couchConfig, serializer );

        couchManager = new CouchManager( couchConfig, couchHttp, serializer, new CouchAppReader() );

        listener =
            dispatcher == null ? null : new CouchChangeListener( dispatcher, couchHttp,
                                                                 couchConfig, couchManager,
                                                                 serializer );
    }

    public DBFixture( final ThreadableListener... listeners )
    {
        couchConfig = new DefaultCouchDBConfiguration( DB_URL );

        serializer = new Serializer();

        couchHttp = new CouchHttpClient( couchConfig, serializer );

        couchManager = new CouchManager( couchConfig, couchHttp, serializer, new CouchAppReader() );

        listener =
            new CouchChangeListener(
                                     new ThreadedChangeDispatcher( Arrays.asList( listeners ),
                                                                   Executors.newFixedThreadPool( 3 ) ),
                                     couchHttp, couchConfig, couchManager, serializer );
    }

    public DBFixture( final WeldContainer weld )
    {
        serializer = new Serializer();
        couchConfig = weld.instance().select( CouchDBConfiguration.class ).get();
        couchHttp = weld.instance().select( CouchHttpClient.class ).get();
        couchManager = weld.instance().select( CouchManager.class ).get();
        listener = weld.instance().select( CouchChangeListener.class ).get();
    }

    public CouchChangeListener getChangeListener()
    {
        return listener;
    }

    public CouchDBConfiguration getCouchConfig()
    {
        return couchConfig;
    }

    public Serializer getSerializer()
    {
        return serializer;
    }

    public CouchHttpClient getCouchHttp()
    {
        return couchHttp;
    }

    public CouchManager getCouchManager()
    {
        return couchManager;
    }

    @Override
    protected void before()
        throws Throwable
    {
        couchManager.dropDatabase();
        couchManager.createDatabase();

        if ( listener != null )
        {
            listener.startup();
        }
    }

    @Override
    protected void after()
    {
        if ( listener != null )
        {
            try
            {
                listener.shutdown();
                while ( listener.isRunning() )
                {
                    synchronized ( listener )
                    {
                        System.out.println( "Waiting 2s for change listener to shutdown..." );
                        try
                        {
                            listener.wait( 2000 );
                        }
                        catch ( InterruptedException e )
                        {
                            break;
                        }
                    }
                }
            }
            catch ( CouchDBException e )
            {
                e.printStackTrace();
            }
        }

        try
        {
            couchManager.dropDatabase();
        }
        catch ( CouchDBException e )
        {
            e.printStackTrace();
        }
    }

    @Singleton
    public static final class ConfigProvider
    {
        private CouchDBConfiguration config;

        private CouchHttpClient httpClient;

        private CouchManager couch;

        private CouchChangeListener changeListener;

        @Inject
        private Serializer serializer;

        @Inject
        private CouchAppReader appReader;

        @Inject
        private CouchChangeDispatcher dispatcher;

        @Produces
        public CouchChangeListener getListener()
        {
            if ( changeListener == null )
            {
                changeListener =
                    new CouchChangeListener( dispatcher, getHttpClient(), getConfig(), getCouch(),
                                             serializer );
            }

            return changeListener;
        }

        @Produces
        public CouchManager getCouch()
        {
            if ( couch == null )
            {
                couch = new CouchManager( getConfig(), getHttpClient(), serializer, appReader );
            }

            return couch;
        }

        @Produces
        public CouchHttpClient getHttpClient()
        {
            if ( httpClient == null )
            {
                httpClient = new CouchHttpClient( getConfig(), serializer );
            }

            return httpClient;
        }

        @Produces
        public CouchDBConfiguration getConfig()
        {
            if ( config == null )
            {
                config = new DefaultCouchDBConfiguration( DB_URL );
            }

            return config;
        }
    }

}
