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
package org.commonjava.couch.change.dispatch;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.event.Observes;
import javax.inject.Singleton;

import org.commonjava.couch.change.CouchDocChange;
import org.commonjava.couch.db.CouchManager;
import org.commonjava.couch.fixture.DBFixture;
import org.commonjava.couch.model.AbstractCouchDocument;
import org.commonjava.couch.util.ChangeSynchronizer;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class J2EECouchChangeDispatcherTest
{

    private final WeldContainer weld = new Weld().initialize();

    @Rule
    public DBFixture dbFix = new DBFixture( weld );

    private TestListener dispatchListener;

    @Before
    public void setupTest()
        throws Exception
    {
        dispatchListener = weld.instance().select( TestListener.class ).get();
    }

    @Test
    public void addDocumentAndDetectCorrespondingChange()
        throws Exception
    {
        boolean stored = getCouch().store( new TestDocument( "addedDocument" ), false );

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
        boolean stored = getCouch().store( doc, false );

        assertThat( stored, equalTo( true ) );

        dispatchListener.waitForChanges( 20 );

        System.out.println( "Checking changes in:" + dispatchListener.changes.hashCode() );
        assertThat( dispatchListener.changes.size(), equalTo( 1 ) );

        System.out.println( "Clearing changes list." );
        dispatchListener.changes.clear();

        getCouch().delete( doc );

        dispatchListener.waitForChanges( 20 );

        System.out.println( "Checking changes in:" + dispatchListener.changes.hashCode() );
        assertThat( dispatchListener.changes.size(), equalTo( 1 ) );
    }

    private CouchManager getCouch()
    {
        return dbFix.getCouchManager();
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

}
