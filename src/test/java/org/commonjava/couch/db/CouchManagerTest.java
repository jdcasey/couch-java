package org.commonjava.couch.db;

import static org.commonjava.couch.fixture.LoggingFixture.setupLogging;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Level;
import org.commonjava.couch.db.action.CouchDocumentAction;
import org.commonjava.couch.db.action.StoreAction;
import org.commonjava.couch.fixture.TestUser;
import org.commonjava.couch.model.CouchApp;
import org.commonjava.couch.model.io.CouchAppReader;
import org.commonjava.couch.model.io.Serializer;
import org.junit.BeforeClass;
import org.junit.Test;

public class CouchManagerTest
{

    private static final String DB_BASE = "http://developer.commonjava.org/db/";

    CouchManager mgr = new CouchManager( new Serializer() );

    @BeforeClass
    public static void initLogging()
    {
        setupLogging( Level.DEBUG );
    }

    @Test
    public void createAndDropDB()
        throws CouchDBException
    {
        String url = DB_BASE + "test-create-drop";

        mgr.dropDatabase( url );

        assertThat( mgr.exists( url ), is( false ) );

        mgr.createDatabase( url );

        assertThat( mgr.exists( url ), is( true ) );

        mgr.dropDatabase( url );

        assertThat( mgr.exists( url ), is( false ) );
    }

    @Test
    public void createDBThenInstallAppThenDropDB()
        throws CouchDBException, IOException
    {
        String url = DB_BASE + "test-create-install-drop";

        mgr.dropDatabase( url );

        assertThat( mgr.exists( url ), is( false ) );

        mgr.createDatabase( url );

        assertThat( mgr.exists( url ), is( true ) );

        CouchApp app = new CouchAppReader().readAppDefinition( "test-app" );

        mgr.installApplication( app, url );

        assertThat( mgr.exists( app, url ), is( true ) );

        mgr.dropDatabase( url );

        assertThat( mgr.exists( app, url ), is( false ) );
        assertThat( mgr.exists( url ), is( false ) );
    }

    @Test
    public void bulkModify_Store_NonTransactional()
        throws Exception
    {
        String url = DB_BASE + "test-create-bulkstore-drop";

        mgr.dropDatabase( url );
        mgr.createDatabase( url );

        assertThat( mgr.exists( url ), is( true ) );

        List<CouchDocumentAction> actions = new ArrayList<CouchDocumentAction>();

        actions.add( new StoreAction( new TestUser( "user1", "User", "Name", "user@nowhere.com" ),
                                      false ) );

        actions.add( new StoreAction(
                                      new TestUser( "user2", "Another", "Name", "user2@nowhere.com" ),
                                      false ) );

        try
        {
            mgr.bulkModify( actions, url, false );
        }
        finally
        {
            try
            {
                mgr.dropDatabase( url );
            }
            catch ( CouchDBException e )
            {
                // Forget it...
                e.printStackTrace();
            }
        }
    }

}
