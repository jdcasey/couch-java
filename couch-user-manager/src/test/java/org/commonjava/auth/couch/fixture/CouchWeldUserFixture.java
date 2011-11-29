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
package org.commonjava.auth.couch.fixture;

import org.cjtest.fixture.CouchWeldFixture;
import org.commonjava.auth.couch.data.UserDataManager;
import org.commonjava.auth.couch.inject.UserDataLiteral;
import org.jboss.weld.environment.se.WeldContainer;

public class CouchWeldUserFixture
    extends CouchWeldFixture
{

    public static final String DB_URL = "http://localhost:5984/test-user-db";

    private final UserDataManager userDataManager;

    private final boolean setupAdminInfo;

    // private final UserManagerConfiguration userConfig;
    //
    // private final PasswordManager passwordManager;
    //
    public CouchWeldUserFixture( final WeldContainer weld )
    {
        super( weld, new UserDataLiteral() );
        // userConfig = weld.instance()
        // .select( UserManagerConfiguration.class )
        // .get();
        // passwordManager = weld.instance()
        // .select( PasswordManager.class )
        // .get();
        userDataManager = weld.instance()
                              .select( UserDataManager.class )
                              .get();
        setupAdminInfo = true;
    }

    public UserDataManager getUserDataManager()
    {
        return userDataManager;
    }

    @Override
    protected void before()
        throws Throwable
    {
        super.before();

        userDataManager.install();

        if ( setupAdminInfo )
        {
            userDataManager.setupAdminInformation();
        }
    }

}
