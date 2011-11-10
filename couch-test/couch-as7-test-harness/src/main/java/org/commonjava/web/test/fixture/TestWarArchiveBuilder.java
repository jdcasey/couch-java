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
package org.commonjava.web.test.fixture;

import java.io.File;
import java.net.URL;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.UrlAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.impl.base.asset.ClassLoaderAsset;
import org.jboss.shrinkwrap.resolver.api.maven.MavenImporter;

public class TestWarArchiveBuilder
{

    protected WebArchive war;

    protected File testPom;

    public TestWarArchiveBuilder( final Class<?> testClass )
    {
        war = ShrinkWrap.create( WebArchive.class, "test.war" )
                        .addAsWebInfResource( new ClassLoaderAsset( "test-beans.xml" ), "beans.xml" );

        war.as( MavenImporter.class )
           .configureFrom( new File( System.getProperty( "user.home" ), ".m2/settings.xml" ).getAbsolutePath() )
           .loadEffectivePom( "pom.xml" )
           .importTestDependencies()
           .importBuildOutput();

        war.addClass( testClass );
    }

    public TestWarArchiveBuilder withExtraClasses( final Class<?>... classes )
    {
        for ( final Class<?> cls : classes )
        {
            war.addClass( cls );
        }

        return this;
    }

    public TestWarArchiveBuilder withLog4jProperties()
    {
        final ClassLoader cloader = Thread.currentThread()
                                          .getContextClassLoader();

        final URL resource = cloader.getResource( "log4j.properties" );
        if ( resource != null )
        {
            war.addAsWebInfResource( new UrlAsset( resource ), "classes/log4j.properties" );
        }
        return this;
    }

    public WebArchive build()
    {
        return war;
    }

}
