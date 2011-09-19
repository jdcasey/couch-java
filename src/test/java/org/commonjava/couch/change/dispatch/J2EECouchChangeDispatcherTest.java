package org.commonjava.couch.change.dispatch;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.Produces;
import javax.inject.Singleton;

import org.commonjava.couch.change.CouchChangeListener;
import org.commonjava.couch.change.CouchDocChange;
import org.commonjava.couch.conf.CouchDBConfiguration;
import org.commonjava.couch.conf.DefaultCouchDBConfiguration;
import org.commonjava.couch.db.CouchManager;
import org.commonjava.couch.model.AbstractCouchDocument;
import org.commonjava.couch.util.ChangeSynchronizer;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class J2EECouchChangeDispatcherTest
{
    private static final String URL = "http://developer.commonjava.org/db/test-change-listener";

    private CouchChangeListener listener;

    private CouchManager couch;

    private TestListener dispatchListener;

    @Before
    public void setupTest()
        throws Exception
    {
        WeldContainer weld = new Weld().initialize();

        couch = weld.instance().select( CouchManager.class ).get();

        couch.dropDatabase();
        couch.createDatabase();

        dispatchListener = weld.instance().select( TestListener.class ).get();

        listener = weld.instance().select( CouchChangeListener.class ).get();
        listener.startup();
    }

    @After
    public void teardownTest()
        throws Exception
    {
        listener.shutdown();
        while ( listener.isRunning() )
        {
            synchronized ( listener )
            {
                System.out.println( "Waiting 2s for change listener to shutdown..." );
                listener.wait( 2000 );
            }
        }

        couch.dropDatabase();
    }

    @Test
    public void addDocumentAndDetectCorrespondingChange()
        throws Exception
    {
        boolean stored = couch.store( new TestDocument( "addedDocument" ), false );

        assertThat( stored, equalTo( true ) );

        dispatchListener.waitForChanges( 20 );

        System.out.println( "Checking changes in:" + dispatchListener.changes.hashCode() );
        assertThat( dispatchListener.changes.size(), equalTo( 1 ) );
    }

    @Test
    public void addDocumentThenDeleteIt_DetectCorrespondingChangesInBetween()
        throws Exception
    {
        TestDocument doc = new TestDocument( "addedDocument" );
        boolean stored = couch.store( doc, false );

        assertThat( stored, equalTo( true ) );

        dispatchListener.waitForChanges( 20 );

        System.out.println( "Checking changes in:" + dispatchListener.changes.hashCode() );
        assertThat( dispatchListener.changes.size(), equalTo( 1 ) );

        System.out.println( "Clearing changes list." );
        dispatchListener.changes.clear();

        couch.delete( doc );

        dispatchListener.waitForChanges( 20 );

        System.out.println( "Checking changes in:" + dispatchListener.changes.hashCode() );
        assertThat( dispatchListener.changes.size(), equalTo( 1 ) );
    }

    @Singleton
    public static final class TestListener
    {

        public final List<CouchDocChange> changes = new ArrayList<CouchDocChange>();

        private final ChangeSynchronizer changeSync = new ChangeSynchronizer();

        public void documentChanged( @Observes final CouchChangeJ2EEEvent change )
        {
            System.out.println( "Adding change: " + change + " to: " + changes.hashCode() );
            changes.add( change.getChange() );
            System.out.println( "Changes list has " + changes.size() + " items." );
            changeSync.setChanged();
        }

        synchronized void waitForChanges( final long seconds )
        {
            System.out.println( "Waiting for changes in:" + changes.hashCode() );
            changeSync.waitForChange( seconds * 1000, 1000 );
        }

    }

    public static final class TestDocument
        extends AbstractCouchDocument
    {

        private String name;

        TestDocument()
        {}

        public TestDocument( final String name )
        {
            this.name = name;
            setCouchDocId( "test:" + name );
        }

        public String getName()
        {
            return name;
        }

        void setName( final String name )
        {
            this.name = name;
        }

    }

    @Singleton
    public static final class ConfigProvider
    {
        private CouchDBConfiguration config;

        @Produces
        public CouchDBConfiguration getConfig()
        {
            if ( config == null )
            {
                config = new DefaultCouchDBConfiguration( URL );
            }

            return config;
        }
    }

}
