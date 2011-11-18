package org.cjtest.fixture;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;

import javax.enterprise.inject.Produces;
import javax.inject.Qualifier;

import org.commonjava.couch.change.CouchChangeListener;
import org.commonjava.couch.conf.CouchDBConfiguration;
import org.commonjava.couch.conf.DefaultCouchDBConfiguration;
import org.commonjava.couch.db.CouchDBException;
import org.commonjava.couch.db.CouchManager;
import org.commonjava.couch.io.CouchHttpClient;
import org.commonjava.couch.io.Serializer;
import org.jboss.weld.environment.se.WeldContainer;
import org.junit.rules.ExternalResource;

public class CouchWeldFixture
    extends ExternalResource
{

    public static final String DB_URL = "http://localhost:5984/test-db";

    private final CouchDBConfiguration couchConfig;

    private final Serializer serializer;

    private final CouchHttpClient couchHttp;

    private final CouchManager couchManager;

    private final CouchChangeListener listener;

    public CouchWeldFixture( final WeldContainer weld, final Annotation... qualifiers )
    {
        serializer = weld.instance()
                         .select( Serializer.class )
                         .get();
        couchConfig = weld.instance()
                          .select( CouchDBConfiguration.class, qualifiers )
                          .get();
        couchHttp = weld.instance()
                        .select( CouchHttpClient.class, qualifiers )
                        .get();
        couchManager = weld.instance()
                           .select( CouchManager.class, qualifiers )
                           .get();
        listener = weld.instance()
                       .select( CouchChangeListener.class, qualifiers )
                       .get();
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

        try
        {
            couchManager.dropDatabase();
        }
        catch ( final CouchDBException e )
        {
            e.printStackTrace();
        }

        super.after();
    }

    protected static Annotation[] getFixtureQualifiers( final Class<? extends CouchWeldFixture> type )
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
