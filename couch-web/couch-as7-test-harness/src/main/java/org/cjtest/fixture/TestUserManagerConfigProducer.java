package org.cjtest.fixture;

import java.util.Properties;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.commonjava.auth.couch.conf.DefaultUserManagerConfig;
import org.commonjava.auth.couch.conf.UserManagerConfiguration;
import org.commonjava.couch.conf.CouchDBConfiguration;
import org.commonjava.couch.conf.DefaultCouchDBConfiguration;
import org.commonjava.web.test.fixture.TestPropertyDefinitions;

@Singleton
public class TestUserManagerConfigProducer
{

    private UserManagerConfiguration umConfig;

    private CouchDBConfiguration couchConfig;

    @Inject
    @Named( TestPropertyDefinitions.NAMED )
    private Properties testProperties;

    @Produces
    public synchronized CouchDBConfiguration getCouchDBConfiguration()
    {
        if ( couchConfig == null )
        {
            couchConfig =
                new DefaultCouchDBConfiguration(
                                                 testProperties.getProperty( TestPropertyDefinitions.DATABASE_URL ) );
        }

        return couchConfig;
    }

    @Produces
    public synchronized UserManagerConfiguration getUserManagerConfiguration()
    {
        if ( umConfig == null )
        {
            umConfig =
                new DefaultUserManagerConfig( "admin@nowhere.com", "password", "Admin", "User" );
        }

        return umConfig;
    }

}
