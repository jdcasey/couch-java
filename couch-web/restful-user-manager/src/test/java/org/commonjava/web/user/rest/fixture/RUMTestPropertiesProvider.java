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
package org.commonjava.web.user.rest.fixture;

import java.util.Properties;

import javax.enterprise.inject.Produces;
import javax.inject.Named;

import org.commonjava.couch.test.fixture.TestPropertyDefinitions;
import org.commonjava.couch.user.fixture.UserTestPropertyDefinitions;

import com.google.inject.Singleton;

@Singleton
public class RUMTestPropertiesProvider
{

    @Produces
    @Named( TestPropertyDefinitions.NAMED )
    public Properties getTestProperties()
    {
        Properties props = new Properties();

        props.put( UserTestPropertyDefinitions.USER_DATABASE_URL,
                   "http://localhost:5984/test-restful-user-manager" );

        return props;
    }

}
