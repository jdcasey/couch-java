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
package org.cjtest.fixture;

import java.util.Properties;

import javax.enterprise.inject.Default;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.commonjava.auth.couch.conf.DefaultUserManagerConfig;
import org.commonjava.auth.couch.conf.UserManagerConfiguration;
import org.commonjava.auth.couch.inject.UserData;
import org.commonjava.couch.conf.CouchDBConfiguration;
import org.commonjava.couch.test.fixture.TestPropertyDefinitions;
import org.commonjava.couch.user.fixture.UserTestPropertyDefinitions;
import org.commonjava.web.test.fixture.TestData;

@Singleton
public class TestUserManagerConfigProducer
{

    private UserManagerConfiguration umConfig;

    @Inject
    @Named( TestPropertyDefinitions.NAMED )
    private Properties testProperties;

    @Produces
    @UserData
    @Default
    public synchronized CouchDBConfiguration getCouchDBConfiguration()
    {
        return getUserManagerConfiguration().getDatabaseConfig();
    }

    @Produces
    @TestData
    @Default
    public synchronized UserManagerConfiguration getUserManagerConfiguration()
    {
        if ( umConfig == null )
        {
            umConfig =
                new DefaultUserManagerConfig(
                                              "admin@nowhere.com",
                                              "password",
                                              "Admin",
                                              "User",
                                              testProperties.getProperty( UserTestPropertyDefinitions.USER_DATABASE_URL ) );
        }

        return umConfig;
    }

}
