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
package org.commonjava.auth.couch.change;

import static org.commonjava.couch.test.fixture.LoggingFixture.setupLogging;

import java.util.HashMap;
import java.util.Map;

import javax.enterprise.inject.Produces;
import javax.inject.Singleton;

import org.apache.log4j.Level;
import org.commonjava.auth.couch.conf.DefaultUserManagerConfig;
import org.commonjava.auth.couch.conf.UserManagerConfiguration;
import org.commonjava.auth.couch.data.UserDataManager;
import org.commonjava.couch.change.CouchChangeListener;
import org.commonjava.couch.conf.CouchDBConfiguration;
import org.commonjava.couch.conf.DefaultCouchDBConfiguration;
import org.commonjava.couch.db.CouchManager;
import org.commonjava.couch.util.ChangeSynchronizer;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.junit.After;
import org.junit.Before;

public abstract class AbstractUserManagerListenerTest
{

    protected CouchChangeListener listener;

    protected CouchManager couch;

    protected UserDataManager dataManager;

    protected WeldContainer weld;

    @Singleton
    public static final class ConfigProvider
    {
        private static final String URL =
            "http://developer.commonjava.org/db/test-user-manager-changes";

        private CouchDBConfiguration config;

        private UserManagerConfiguration umConfig;

        @Produces
        public UserManagerConfiguration getUserManagerConfig()
        {
            if ( umConfig == null )
            {
                DefaultUserManagerConfig c = new DefaultUserManagerConfig();
                c.setAdminPassword( "password" );
                umConfig = c;
            }

            return umConfig;
        }

        @Produces
        public CouchDBConfiguration getConfig()
        {
            if ( config == null )
            {
                config = new DefaultCouchDBConfiguration( URL );
            }

            return config;
        }
    }

    @Before
    public void setupTest()
        throws Exception
    {
        Map<String, Level> customEntries = new HashMap<String, Level>();
        customEntries.put( ChangeSynchronizer.class.getName(), Level.DEBUG );

        setupLogging( Level.INFO, customEntries );

        weld = new Weld().initialize();

        couch = weld.instance().select( CouchManager.class ).get();
        couch.dropDatabase();

        dataManager = weld.instance().select( UserDataManager.class ).get();
        dataManager.install();
        dataManager.setupAdminInformation();

        listener = weld.instance().select( CouchChangeListener.class ).get();
        listener.startup();

        setupFixtures();
    }

    protected abstract void setupFixtures();

    @After
    public void teardownTest()
        throws Exception
    {
        listener.shutdown();
        while ( listener.isRunning() )
        {
            synchronized ( listener )
            {
                System.out.println( "Waiting 2s for change listener to shutdown..." );
                listener.wait( 2000 );
            }
        }

        couch.dropDatabase();
    }

    public AbstractUserManagerListenerTest()
    {
        super();
    }

}
