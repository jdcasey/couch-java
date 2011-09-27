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

import org.commonjava.couch.change.CouchDocChange;
import org.commonjava.couch.db.CouchManager;
import org.commonjava.couch.fixture.DBFixture;
import org.commonjava.couch.model.AbstractCouchDocument;
import org.commonjava.couch.util.ChangeSynchronizer;
import org.junit.Rule;
import org.junit.Test;

public class SimpleCouchChangeDispatcherTest
{

    private final TestListener dispatchListener = new TestListener();

    @Rule
    public DBFixture dbFix = new DBFixture( dispatchListener );

    @Test
    public void addDocumentAndDetectCorrespondingChange()
        throws Exception
    {
        boolean stored = getCouch().store( new TestDocument( "addedDocument" ), false );

        assertThat( stored, equalTo( true ) );

        dispatchListener.waitForChange( 20000, 1000 );

        assertThat( dispatchListener.changes.size(), equalTo( 1 ) );
    }

    @Test
    public void addDocumentThenDeleteIt_DetectCorrespondingChangesInBetween()
        throws Exception
    {
        TestDocument doc = new TestDocument( "addedDocument" );
        boolean stored = getCouch().store( doc, false );

        assertThat( stored, equalTo( true ) );

        dispatchListener.waitForChange( 20000, 1000 );

        assertThat( dispatchListener.changes.size(), equalTo( 1 ) );

        dispatchListener.changes.clear();

        getCouch().delete( doc );

        dispatchListener.waitForChange( 20000, 1000 );

        assertThat( dispatchListener.changes.size(), equalTo( 1 ) );
    }

    private CouchManager getCouch()
    {
        return dbFix.getCouchManager();
    }

    public static final class TestListener
        implements ThreadableListener
    {

        public final List<CouchDocChange> changes = new ArrayList<CouchDocChange>();

        private final ChangeSynchronizer changeSync = new ChangeSynchronizer();

        @Override
        public void documentChanged( final CouchDocChange change )
        {
            System.out.println( "Adding change: " + change );
            changes.add( change );
            changeSync.setChanged();
        }

        @Override
        public synchronized void waitForChange( final long totalMillis, final long pollingMillis )
        {
            changeSync.waitForChange( totalMillis, pollingMillis );
        }

        @Override
        public boolean canProcess( final String id, final boolean deleted )
        {
            return true;
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
