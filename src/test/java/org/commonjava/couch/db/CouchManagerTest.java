/*******************************************************************************
 * Copyright (C) 2011  John Casey
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
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

}
