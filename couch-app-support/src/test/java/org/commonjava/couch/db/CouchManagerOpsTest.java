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
package org.commonjava.couch.db;

import static org.commonjava.couch.fixture.LoggingFixture.setupLogging;
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
import org.commonjava.couch.fixture.DBFixture;
import org.commonjava.couch.fixture.TestUser;
import org.commonjava.couch.io.CouchAppReader;
import org.commonjava.couch.model.CouchApp;
import org.commonjava.couch.model.CouchDocRef;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

public class CouchManagerOpsTest
{

    @Rule
    public DBFixture dbFix = new DBFixture();

    @BeforeClass
    public static void initLogging()
    {
        setupLogging( Level.DEBUG );
    }

    @Test
    public void storeAndGetDocument()
        throws Exception
    {
        getCouch().store( new TestUser( "user", "User", "Name", "email@nowhere.com" ), false );

        TestUser user = getCouch().getDocument( new CouchDocRef( "user" ), TestUser.class );
        assertThat( user, notNullValue() );
        assertThat( user.getUsername(), equalTo( "user" ) );
        assertThat( user.getFirst(), equalTo( "User" ) );
        assertThat( user.getLast(), equalTo( "Name" ) );
        assertThat( user.getEmail(), equalTo( "email@nowhere.com" ) );
    }

    @Test
    public void storeThreeAndBulkGetTwoDocuments()
        throws Exception
    {
        TestUser u1 = new TestUser( "user", "User", "Name", "email@nowhere.com" );
        TestUser u2 = new TestUser( "user2", "User", "Name2", "email2@nowhere.com" );
        TestUser u3 = new TestUser( "user3", "User", "Name3", "email3@nowhere.com" );

        getCouch().store( u1, false );
        getCouch().store( u2, false );
        getCouch().store( u3, false );

        List<TestUser> users =
            getCouch().getDocuments( TestUser.class, new CouchDocRef( "user" ),
                                     new CouchDocRef( "user3" ) );

        assertThat( users.size(), equalTo( 2 ) );
        assertThat( users.contains( u1 ), equalTo( true ) );
        assertThat( users.contains( u2 ), equalTo( false ) );
        assertThat( users.contains( u3 ), equalTo( true ) );
    }

    @Test
    public void storeAndReadUserListingFromView()
        throws Exception
    {
        CouchApp app =
            new CouchAppReader().readAppDefinition( new SimpleAppDescription( "test-app",
                                                                              "test-app",
                                                                              "test-view" ) );

        getCouch().installApplication( app );

        getCouch().store( new TestUser( "user1", "User", "Name", "user@nowhere.com" ), false );
        getCouch().store( new TestUser( "user2", "Another", "Name", "user2@nowhere.com" ), false );

        ViewRequest req = new ViewRequest( "test-app", "test-view" );
        List<TestUser> users = getCouch().getViewListing( req, TestUser.class );

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

        getCouch().modify( actions, false );
    }

    private CouchManager getCouch()
    {
        return dbFix.getCouchManager();
    }

}
