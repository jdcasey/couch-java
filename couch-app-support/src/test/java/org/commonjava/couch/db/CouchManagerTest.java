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
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;

import org.apache.log4j.Level;
import org.commonjava.couch.conf.CouchDBConfiguration;
import org.commonjava.couch.conf.DefaultCouchDBConfiguration;
import org.commonjava.couch.db.model.SimpleAppDescription;
import org.commonjava.couch.io.CouchAppReader;
import org.commonjava.couch.io.CouchHttpClient;
import org.commonjava.couch.io.Serializer;
import org.commonjava.couch.model.CouchApp;
import org.junit.BeforeClass;
import org.junit.Test;

public class CouchManagerTest
{

    private static final String DB_URL = "http://developer.commonjava.org/db/test-couch-manager";

    private final CouchDBConfiguration config = new DefaultCouchDBConfiguration( DB_URL );

    private final Serializer serializer = new Serializer();

    private final CouchManager mgr = new CouchManager( config, new CouchHttpClient( config,
                                                                                    serializer ),
                                                       serializer, new CouchAppReader() );

    @BeforeClass
    public static void initLogging()
    {
        setupLogging( Level.DEBUG );
    }

    @Test
    public void createAndDropDB()
        throws CouchDBException
    {
        mgr.dropDatabase();

        assertThat( mgr.exists( "/" ), is( false ) );

        mgr.createDatabase();

        assertThat( mgr.exists( "/" ), is( true ) );

        mgr.dropDatabase();

        assertThat( mgr.exists( "/" ), is( false ) );
    }

    @Test
    public void createDBThenInstallAppThenDropDB()
        throws CouchDBException, IOException
    {
        mgr.dropDatabase();

        assertThat( mgr.exists( "/" ), is( false ) );

        mgr.createDatabase();

        assertThat( mgr.exists( "/" ), is( true ) );

        CouchApp app =
            new CouchAppReader().readAppDefinition( new SimpleAppDescription( "test-app" ) );

        mgr.installApplication( app );

        assertThat( mgr.exists( app ), is( true ) );

        mgr.dropDatabase();

        assertThat( mgr.exists( app ), is( false ) );
        assertThat( mgr.exists( "/" ), is( false ) );
    }

}
