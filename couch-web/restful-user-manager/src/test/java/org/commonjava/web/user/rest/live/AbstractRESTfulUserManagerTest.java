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
package org.commonjava.web.user.rest.live;

import javax.inject.Inject;

import org.cjtest.fixture.TestAuthenticationControls;
import org.commonjava.auth.couch.inject.UserData;
import org.commonjava.couch.db.CouchManager;
import org.commonjava.couch.user.web.test.AbstractUserRESTCouchTest;
import org.commonjava.web.test.fixture.WebFixture;
import org.junit.Rule;

public abstract class AbstractRESTfulUserManagerTest
    extends AbstractUserRESTCouchTest
{

    @Inject
    @UserData
    private CouchManager couch;

    @Inject
    protected TestAuthenticationControls controls;

    @Rule
    public WebFixture fixture = new WebFixture();

    protected AbstractRESTfulUserManagerTest()
    {
    }

    @Override
    protected CouchManager getCouchManager()
    {
        return couch;
    }

}
