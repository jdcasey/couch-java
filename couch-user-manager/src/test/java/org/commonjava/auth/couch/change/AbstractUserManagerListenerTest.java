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
package org.commonjava.auth.couch.change;

import static org.commonjava.couch.test.fixture.LoggingFixture.setupLogging;

import org.apache.log4j.Level;
import org.commonjava.auth.couch.data.UserDataManager;
import org.commonjava.auth.couch.fixture.CouchWeldUserFixture;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;

public abstract class AbstractUserManagerListenerTest
{

    protected final WeldContainer weld = new Weld().initialize();

    @Rule
    public CouchWeldUserFixture fixture = new CouchWeldUserFixture( weld );

    @BeforeClass
    public static void logging()
    {
        setupLogging( Level.DEBUG );

    }

    @Before
    public final void setupTest()
        throws Exception
    {
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
