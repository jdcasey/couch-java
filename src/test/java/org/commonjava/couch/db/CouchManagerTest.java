package org.commonjava.couch.db;

import static org.commonjava.couch.fixture.LoggingFixture.setupLogging;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;

import org.apache.log4j.Level;
import org.commonjava.couch.model.CouchApp;
import org.commonjava.couch.model.io.CouchAppReader;
import org.commonjava.couch.model.io.Serializer;
import org.junit.BeforeClass;
import org.junit.Test;

public class CouchManagerTest
{

    private static final String DB_BASE = "http://developer.commonjava.org/db/";

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
        CouchManager mgr = new CouchManager( new Serializer() );

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
        CouchManager mgr = new CouchManager( new Serializer() );

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

}
