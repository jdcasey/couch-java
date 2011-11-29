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

import javax.enterprise.inject.Default;
import javax.enterprise.inject.Produces;
import javax.inject.Singleton;

import org.commonjava.auth.couch.conf.DefaultUserManagerConfig;
import org.commonjava.auth.couch.conf.UserManagerConfiguration;
import org.commonjava.auth.couch.inject.UserData;
import org.commonjava.couch.conf.CouchDBConfiguration;

@Singleton
public class UserConfigProvider
{

    public static final String DB_URL = "http://localhost:5984/test-user-db";

    private UserManagerConfiguration umConfig;

    @Produces
    public UserManagerConfiguration getUserManagerConfig()
    {
        if ( umConfig == null )
        {
            final DefaultUserManagerConfig c = new DefaultUserManagerConfig();
            c.setAdminPassword( "password" );
            c.setDatabaseUrl( DB_URL );
            umConfig = c;
        }

        return umConfig;
    }

    @Produces
    @UserData
    @Default
    public CouchDBConfiguration getConfig()
    {
        return getUserManagerConfig().getDatabaseConfig();
    }
}
