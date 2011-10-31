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
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.Collections;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.DefaultModelReader;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.asset.UrlAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.MavenImporter;
import org.junit.rules.ExternalResource;

public class TestWarArchiveBuilder
    extends ExternalResource
{

    protected WebArchive war;

    protected File testPom;

    public TestWarArchiveBuilder loadFromMaven( final File pomFile )
        throws IOException
    {
        war = ShrinkWrap.create( WebArchive.class, "test.war" );

        Model model =
            new DefaultModelReader().read( pomFile, Collections.<String, Object> emptyMap() );

        if ( !"war".equals( model.getPackaging() ) )
        {
            testPom = new File( pomFile.getAbsoluteFile().getParentFile(), "pom.test-war.xml" );
            model.setPackaging( "war" );
            FileWriter writer = null;
            try
            {
                writer = new FileWriter( testPom );
                new MavenXpp3Writer().write( writer, model );
            }
            finally
            {
                IOUtils.closeQuietly( writer );
            }

            war.addAsWebInfResource( EmptyAsset.INSTANCE, "web.xml" );
        }
        else
        {
            testPom = pomFile;
        }

        war.as( MavenImporter.class ).loadEffectivePom( testPom.getAbsolutePath() ).importTestBuildOutput().importBuildOutput().importTestDependencies();

        return this;
    }

    @Override
    protected void after()
    {
        if ( testPom != null )
        {
            String path = testPom.getAbsolutePath();
            try
            {
                FileUtils.forceDelete( testPom );
            }
            catch ( IOException e )
            {
                System.err.println( "Failed to delete test WAR POM: " + path );
            }
        }
    }

    public TestWarArchiveBuilder withAllStandards()
    {
        // return withStandardPackages().withEmptyBeansXml().withLog4jProperties();
        return withEmptyBeansXml().withLog4jProperties();
    }

    public TestWarArchiveBuilder withEmptyBeansXml()
    {
        war.addAsWebInfResource( EmptyAsset.INSTANCE, "beans.xml" );

        return this;
    }

    public TestWarArchiveBuilder withLog4jProperties()
    {
        ClassLoader cloader = Thread.currentThread().getContextClassLoader();

        URL resource = cloader.getResource( "log4j.properties" );
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
