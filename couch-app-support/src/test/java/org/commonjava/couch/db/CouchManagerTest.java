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

import static org.commonjava.couch.fixture.LoggingFixture.setupLogging;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;

import org.apache.log4j.Level;
import org.commonjava.couch.db.model.SimpleAppDescription;
import org.commonjava.couch.fixture.DBFixture;
import org.commonjava.couch.io.CouchAppReader;
import org.commonjava.couch.model.CouchApp;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

public class CouchManagerTest
{

    @Rule
    public DBFixture dbFix = new DBFixture();

    @BeforeClass
    public static void initLogging()
    {
        setupLogging( Level.DEBUG );
    }

    @Test
    public void createAndDropDB()
        throws CouchDBException
    {
        getCouch().dropDatabase();

        assertThat( getCouch().exists( "/" ), is( false ) );

        getCouch().createDatabase();

        assertThat( getCouch().exists( "/" ), is( true ) );

        getCouch().dropDatabase();

        assertThat( getCouch().exists( "/" ), is( false ) );
    }

    @Test
    public void createDBThenInstallAppThenDropDB()
        throws CouchDBException, IOException
    {
        getCouch().dropDatabase();

        assertThat( getCouch().exists( "/" ), is( false ) );

        getCouch().createDatabase();

        assertThat( getCouch().exists( "/" ), is( true ) );

        CouchApp app =
            new CouchAppReader().readAppDefinition( new SimpleAppDescription( "test-app" ) );

        getCouch().installApplication( app );

        assertThat( getCouch().exists( app ), is( true ) );

        getCouch().dropDatabase();

        assertThat( getCouch().exists( app ), is( false ) );
        assertThat( getCouch().exists( "/" ), is( false ) );
    }

    private CouchManager getCouch()
    {
        return dbFix.getCouchManager();
    }

}
