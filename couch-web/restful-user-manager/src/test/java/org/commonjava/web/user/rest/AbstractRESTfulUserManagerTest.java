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
package org.commonjava.web.user.rest;

import javax.inject.Inject;

import org.cjtest.fixture.TestUserManagerConfigProducer;
import org.commonjava.auth.couch.data.UserAppDescription;
import org.commonjava.auth.couch.inject.UserData;
import org.commonjava.couch.db.CouchManager;
import org.commonjava.couch.io.CouchHttpClient;
import org.commonjava.web.test.AbstractRESTCouchTest;
import org.commonjava.web.test.fixture.TestWarArchiveBuilder;
import org.commonjava.web.user.rest.fixture.RUMTestPropertiesProvider;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.spec.WebArchive;

public abstract class AbstractRESTfulUserManagerTest
    extends AbstractRESTCouchTest
{

    @Inject
    @UserData
    private CouchManager couch;

    @Deployment
    public static WebArchive createTestWar()
    {
        TestWarArchiveBuilder builder =
            new TestWarArchiveBuilder( TestUserManagerConfigProducer.class );

        builder.withExtraPackages( true, CouchHttpClient.class.getPackage(),
                                   RUMTestPropertiesProvider.class.getPackage() );

        builder.withAllStandards();
        builder.withTestRESTApplication();
        builder.withTestUserManagerConfigProducer();
        builder.withApplication( new UserAppDescription() );
        builder.withExtraPackages( true, "org.commonjava" );

        return builder.build();
    }

    protected AbstractRESTfulUserManagerTest()
    {}

    @Override
    protected CouchManager getCouchManager()
    {
        return couch;
    }

}
