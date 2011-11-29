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

import org.cjtest.fixture.CouchFixture;
import org.commonjava.auth.couch.conf.DefaultUserManagerConfig;
import org.commonjava.auth.couch.conf.UserManagerConfiguration;
import org.commonjava.auth.couch.data.PasswordManager;
import org.commonjava.auth.couch.data.UserDataManager;
import org.commonjava.auth.couch.inject.UserData;

@UserData
public class CouchUserFixture
    extends CouchFixture
{

    private final UserDataManager userDataManager;

    private final boolean setupAdminInfo;

    private final UserManagerConfiguration userConfig;

    private final PasswordManager passwordManager;

    public CouchUserFixture()
    {
        super( UserConfigProvider.DB_URL );
        this.userConfig = new DefaultUserManagerConfig();
        this.passwordManager = new PasswordManager();
        this.userDataManager = new UserDataManager( userConfig, passwordManager, getCouchManager() );

        setupAdminInfo = false;
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
