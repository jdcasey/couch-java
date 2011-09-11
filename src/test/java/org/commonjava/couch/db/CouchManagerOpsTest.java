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
package org.commonjava.couch.db;

import static org.commonjava.couch.test.fixture.LoggingFixture.setupLogging;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Level;
import org.commonjava.couch.db.action.CouchDocumentAction;
import org.commonjava.couch.db.action.StoreAction;
import org.commonjava.couch.db.model.SimpleAppDescription;
import org.commonjava.couch.db.model.ViewRequest;
import org.commonjava.couch.fixture.TestUser;
import org.commonjava.couch.model.CouchApp;
import org.commonjava.couch.model.CouchDocRef;
import org.commonjava.couch.model.io.CouchAppReader;
import org.commonjava.couch.model.io.Serializer;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class CouchManagerOpsTest
{

    private static final String DB_URL = "http://developer.commonjava.org/db/test-ops";

    CouchManager mgr = new CouchManager( new Serializer(), new CouchAppReader() );

    @BeforeClass
    public static void initLogging()
    {
        setupLogging( Level.DEBUG );
    }

    @Before
    public void setupDb()
        throws Exception
    {
        mgr.dropDatabase( DB_URL );
        mgr.createDatabase( DB_URL );
    }

    @After
    public void teardownDb()
        throws Exception
    {
        mgr.dropDatabase( DB_URL );
    }

    @Test
    public void storeAndGetDocument()
        throws Exception
    {
        mgr.store( new TestUser( "user", "User", "Name", "email@nowhere.com" ), DB_URL, false );

        TestUser user = mgr.getDocument( new CouchDocRef( "user" ), DB_URL, TestUser.class );
        assertThat( user, notNullValue() );
        assertThat( user.getUsername(), equalTo( "user" ) );
        assertThat( user.getFirst(), equalTo( "User" ) );
        assertThat( user.getLast(), equalTo( "Name" ) );
        assertThat( user.getEmail(), equalTo( "email@nowhere.com" ) );
    }

    @Test
    public void storeAndReadUserListingFromView()
        throws Exception
    {
        CouchApp app =
            new CouchAppReader().readAppDefinition( new SimpleAppDescription( "test-app",
                                                                              "test-app",
                                                                              "test-view" ) );

        mgr.installApplication( app, DB_URL );

        mgr.store( new TestUser( "user1", "User", "Name", "user@nowhere.com" ), DB_URL, false );
        mgr.store( new TestUser( "user2", "Another", "Name", "user2@nowhere.com" ), DB_URL, false );

        ViewRequest req = new ViewRequest( "test-app", "test-view" );
        List<TestUser> users = mgr.getViewListing( req, DB_URL, TestUser.class );

        assertThat( users, notNullValue() );
        assertThat( users.size(), equalTo( 2 ) );

        System.out.println( users );
    }

    @Test
    public void bulkModify_Store_NonTransactional()
        throws Exception
    {
        List<CouchDocumentAction> actions = new ArrayList<CouchDocumentAction>();

        actions.add( new StoreAction( new TestUser( "user1", "User", "Name", "user@nowhere.com" ),
                                      false ) );

        actions.add( new StoreAction(
                                      new TestUser( "user2", "Another", "Name", "user2@nowhere.com" ),
                                      false ) );

        mgr.modify( actions, DB_URL, false );
    }

}
