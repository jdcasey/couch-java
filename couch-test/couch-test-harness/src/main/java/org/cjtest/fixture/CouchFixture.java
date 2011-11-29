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
package org.cjtest.fixture;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;

import javax.enterprise.inject.Produces;
import javax.inject.Qualifier;

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
import org.junit.rules.ExternalResource;

public class CouchFixture
    extends ExternalResource
{

    public static final String DB_URL = "http://localhost:5984/test-db";

    private final CouchDBConfiguration couchConfig;

    private final Serializer serializer;

    private final CouchHttpClient couchHttp;

    private final CouchManager couchManager;

    private final CouchChangeListener listener;

    private boolean debug = false;

    public CouchFixture()
    {
        this( (CouchChangeDispatcher) null, DB_URL );
    }

    public CouchFixture( final String url )
    {
        this( (CouchChangeDispatcher) null, url );
    }

    public CouchFixture( final CouchChangeDispatcher dispatcher, final String dbUrl )
    {
        couchConfig = new DefaultCouchDBConfiguration( dbUrl );

        serializer = new Serializer();

        couchHttp = new CouchHttpClient( couchConfig, serializer );

        couchManager = new CouchManager( couchConfig, couchHttp, serializer, new CouchAppReader() );

        listener =
            dispatcher == null ? null : new CouchChangeListener( dispatcher, couchHttp, couchConfig, couchManager,
                                                                 serializer );
    }

    public CouchFixture( final String dbUrl, final ThreadableListener... listeners )
    {
        couchConfig = new DefaultCouchDBConfiguration( dbUrl );

        serializer = new Serializer();

        couchHttp = new CouchHttpClient( couchConfig, serializer );

        couchManager = new CouchManager( couchConfig, couchHttp, serializer, new CouchAppReader() );

        listener =
            new CouchChangeListener( new ThreadedChangeDispatcher( Arrays.asList( listeners ),
                                                                   Executors.newFixedThreadPool( 3 ) ), couchHttp,
                                     couchConfig, couchManager, serializer );
    }

    // public CouchFixture( final WeldContainer weld, final Annotation... qualifiers )
    // {
    // serializer = weld.instance().select( Serializer.class ).get();
    // couchConfig = weld.instance().select( CouchDBConfiguration.class, qualifiers ).get();
    // couchHttp = weld.instance().select( CouchHttpClient.class, qualifiers ).get();
    // couchManager = weld.instance().select( CouchManager.class, qualifiers ).get();
    // listener = weld.instance().select( CouchChangeListener.class, qualifiers ).get();
    // }

    public void setDebug( final boolean debug )
    {
        this.debug = debug;
    }

    public boolean isDebug()
    {
        return debug;
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
        super.before();

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
                        catch ( final InterruptedException e )
                        {
                            break;
                        }
                    }
                }
            }
            catch ( final CouchDBException e )
            {
                e.printStackTrace();
            }
        }

        if ( !debug )
        {
            try
            {
                couchManager.dropDatabase();
            }
            catch ( final CouchDBException e )
            {
                e.printStackTrace();
            }
        }

        super.after();
    }

    protected static Annotation[] getFixtureQualifiers( final Class<? extends CouchFixture> type )
    {
        final Annotation[] annotations = type.getAnnotations();
        final Set<Annotation> annos = new HashSet<Annotation>();
        for ( final Annotation annotation : annotations )
        {
            final Class<?> annoCls = annotation.annotationType();
            final Annotation[] aannos = annoCls.getAnnotations();
            for ( final Annotation aanno : aannos )
            {
                if ( aanno instanceof Qualifier )
                {
                    annos.add( annotation );
                    break;
                }
            }
        }

        return annos.toArray( new Annotation[] {} );
    }

    protected abstract static class FixtureConfigProvider
    {
        private CouchDBConfiguration config;

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
