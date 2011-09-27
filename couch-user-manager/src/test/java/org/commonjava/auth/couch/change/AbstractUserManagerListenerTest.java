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

import org.apache.log4j.Level;
import org.commonjava.auth.couch.data.UserDataManager;
import org.commonjava.auth.couch.fixture.CouchUserFixture;
import org.commonjava.couch.util.ChangeSynchronizer;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.junit.Before;
import org.junit.Rule;

public abstract class AbstractUserManagerListenerTest
{

    protected final WeldContainer weld = new Weld().initialize();

    @Rule
    public CouchUserFixture fixture = new CouchUserFixture( weld );

    @Before
    public final void setupTest()
        throws Exception
    {
        Map<String, Level> customEntries = new HashMap<String, Level>();
        customEntries.put( ChangeSynchronizer.class.getName(), Level.DEBUG );

        setupLogging( Level.INFO, customEntries );

        setupFixtures();
    }

    protected final UserDataManager getDataManager()
    {
        return fixture.getUserDataManager();
    }

    protected abstract void setupFixtures();

    protected AbstractUserManagerListenerTest()
    {
        super();
    }

}
