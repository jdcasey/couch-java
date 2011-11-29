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
