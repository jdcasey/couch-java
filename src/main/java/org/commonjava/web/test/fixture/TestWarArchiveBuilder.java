package org.commonjava.web.test.fixture;

import static org.junit.Assert.fail;

import java.net.URL;

import org.cjtest.fixture.TestAuthenticationFilter;
import org.cjtest.fixture.TestRESTApplication;
import org.cjtest.fixture.TestUserManagerConfigProducer;
import org.commonjava.auth.couch.conf.UserManagerConfiguration;
import org.commonjava.auth.couch.data.UserDataManager;
import org.commonjava.auth.couch.model.User;
import org.commonjava.auth.couch.util.IdUtils;
import org.commonjava.auth.shiro.couch.CouchRealm;
import org.commonjava.couch.db.CouchManager;
import org.commonjava.couch.db.model.AppDescription;
import org.commonjava.couch.model.CouchDocument;
import org.commonjava.couch.test.fixture.LoggingFixture;
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
        war.addPackages( true, Logger.class.getPackage(), JsonSerializer.class.getPackage(),
                         Listing.class.getPackage(), UrlUtils.class.getPackage(),
                         CouchManager.class.getPackage(), CouchDocument.class.getPackage(),
                         IdUtils.class.getPackage(), User.class.getPackage(),
                         UserDataManager.class.getPackage(),
                         UserManagerConfiguration.class.getPackage(),
                         LoggingFixture.class.getPackage(),
                         AbstractRESTCouchTest.class.getPackage(), CouchRealm.class.getPackage() );

        war.addPackages( true, "org.apache.http" );
        war.addPackages( true, "org.apache.shiro" );
        war.addPackages( true, "org.apache.commons.lang" );
        war.addPackages( true, "org.apache.commons.codec" );
        war.addPackages( true, "org.apache.commons.io" );
        war.addPackages( true, "org.apache.log4j" );
        war.addPackages( true, "com.google.gson" );
        war.addPackages( true, "org.slf4j" );
        war.addPackages( true, "org.apache.commons.logging" );

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
