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
package org.commonjava.web.user.rest.live;

import static org.junit.Assert.fail;

import java.io.File;

import javax.inject.Inject;

import org.commonjava.auth.couch.inject.UserData;
import org.commonjava.couch.db.CouchManager;
import org.commonjava.couch.user.web.test.AbstractUserRESTCouchTest;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.impl.base.asset.ClassLoaderAsset;
import org.jboss.shrinkwrap.resolver.api.maven.MavenImporter;

public abstract class AbstractRESTfulUserManagerTest
    extends AbstractUserRESTCouchTest
{

    @Inject
    @UserData
    private CouchManager couch;

    public static WebArchive createTestWar( final Class<?> testClass )
    {
        final WebArchive war = ShrinkWrap.create( WebArchive.class, "test.war" )
                                         .addAsWebInfResource( new ClassLoaderAsset( "test-beans.xml" ), "beans.xml" );

        war.as( MavenImporter.class )
           .configureFrom( new File( System.getProperty( "user.home" ), ".m2/settings.xml" ).getAbsolutePath() )
           .loadEffectivePom( "pom.xml" )
           .importTestDependencies()
           .importBuildOutput();

        if ( testClass == null )
        {
            fail( "testClass field not specified in: " + AbstractRESTfulUserManagerTest.class.getName()
                + ". Cannot create WAR." );
        }

        war.addClass( AbstractRESTfulUserManagerTest.class );
        war.addClass( testClass );

        return war;
    }

    protected AbstractRESTfulUserManagerTest()
    {
    }

    @Override
    protected CouchManager getCouchManager()
    {
        return couch;
    }

}
