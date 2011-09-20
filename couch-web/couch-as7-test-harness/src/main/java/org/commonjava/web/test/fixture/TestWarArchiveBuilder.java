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

import static org.junit.Assert.fail;

import java.net.URL;

import org.cjtest.fixture.TestAuthenticationFilter;
import org.cjtest.fixture.TestRESTApplication;
import org.cjtest.fixture.TestUserManagerConfigProducer;
import org.commonjava.auth.couch.conf.UserManagerConfiguration;
import org.commonjava.auth.couch.data.UserDataManager;
import org.commonjava.auth.couch.model.User;
import org.commonjava.auth.shiro.couch.CouchRealm;
import org.commonjava.couch.change.CouchChangeListener;
import org.commonjava.couch.conf.CouchDBConfiguration;
import org.commonjava.couch.db.CouchManager;
import org.commonjava.couch.db.model.AppDescription;
import org.commonjava.couch.io.CouchHttpClient;
import org.commonjava.couch.model.CouchDocument;
import org.commonjava.couch.test.fixture.LoggingFixture;
import org.commonjava.couch.util.IdUtils;
import org.commonjava.couch.util.UrlUtils;
import org.commonjava.util.logging.Logger;
import org.commonjava.web.common.model.Listing;
import org.commonjava.web.common.ser.JsonSerializer;
import org.commonjava.web.test.AbstractRESTCouchTest;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.asset.UrlAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;

public class TestWarArchiveBuilder
{

    private static final Package[] STD_PACKAGES = {
        Logger.class.getPackage(),
        CouchHttpClient.class.getPackage(),
        CouchChangeListener.class.getPackage(),
        JsonSerializer.class.getPackage(),
        Listing.class.getPackage(),
        UrlUtils.class.getPackage(),
        CouchManager.class.getPackage(),
        CouchDBConfiguration.class.getPackage(),
        CouchDocument.class.getPackage(),
        IdUtils.class.getPackage(),
        User.class.getPackage(),
        UserDataManager.class.getPackage(),
        UserManagerConfiguration.class.getPackage(),
        LoggingFixture.class.getPackage(),
        AbstractRESTCouchTest.class.getPackage(),
        CouchRealm.class.getPackage() };

    private static final String[] STD_PACKAGE_ROOTS = {
        "org.apache.http",
        "org.apache.shiro",
        "org.apache.commons.lang",
        "org.apache.commons.codec",
        "org.apache.commons.io",
        "org.apache.log4j",
        "com.google.gson",
        "org.slf4j",
        "org.apache.commons.logging" };

    private final WebArchive war;

    public TestWarArchiveBuilder( final Class<?> testPropertiesProducer )
    {
        this( "test", testPropertiesProducer );
    }

    public TestWarArchiveBuilder( final String warName, final Class<?> testPropertiesProducer )
    {
        war = ShrinkWrap.create( WebArchive.class, warName + ".war" );
        war.addClass( testPropertiesProducer );
    }

    public TestWarArchiveBuilder withExtraPackages( final boolean recursive, final String... extras )
    {
        war.addPackages( recursive, extras );

        return this;
    }

    public TestWarArchiveBuilder withExtraPackages( final boolean recursive,
                                                    final Package... extras )
    {
        war.addPackages( recursive, extras );

        return this;
    }

    public TestWarArchiveBuilder withExtraClasses( final Class<?>... extras )
    {
        war.addClasses( extras );

        return this;
    }

    public TestWarArchiveBuilder withAllStandards()
    {
        return withStandardContents().withStandardAuthentication();
    }

    public TestWarArchiveBuilder withTestUserManagerConfigProducer()
    {
        war.addClass( TestUserManagerConfigProducer.class );

        return this;
    }

    public TestWarArchiveBuilder withStandardAuthentication()
    {
        war.addClass( TestAuthenticationFilter.class );

        return this;
    }

    public TestWarArchiveBuilder withApplication( final AppDescription description )
    {
        ClassLoader cloader = Thread.currentThread().getContextClassLoader();

        String base = "couchapps/" + description.getClasspathAppResource() + "/views/";
        for ( String viewName : description.getViewNames() )
        {
            String path = base + viewName + "/map.js";
            URL resource = cloader.getResource( path );
            if ( resource == null )
            {
                fail( "Cannot find view: " + viewName + " for CouchDB application: "
                    + description.getAppName() + " (classpath: " + path + ")" );
            }

            System.out.println( "Adding app resource: " + path );
            war.addAsWebInfResource( new UrlAsset( resource ), "classes/" + path );

            path = base + viewName + "/reduce.js";
            resource = cloader.getResource( path );
            if ( resource != null )
            {
                System.out.println( "Adding app resource: " + path );
                war.addAsWebInfResource( new UrlAsset( resource ), "classes/" + path );
            }
        }

        return this;
    }

    public TestWarArchiveBuilder withTestRESTApplication()
    {
        war.addClass( TestRESTApplication.class );

        return this;
    }

    public TestWarArchiveBuilder withStandardContents()
    {
        war.addPackages( true, STD_PACKAGES );
        war.addPackages( true, STD_PACKAGE_ROOTS );

        war.addAsWebInfResource( EmptyAsset.INSTANCE, "beans.xml" );

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
