package org.cjtest.fixture;

import java.util.Properties;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;

import org.commonjava.auth.couch.conf.DefaultUserManagerConfig;
import org.commonjava.auth.couch.conf.UserManagerConfiguration;
import org.commonjava.web.test.fixture.TestPropertyDefinitions;

public class TestUserManagerConfigProducer
{

    private static UserManagerConfiguration umConfig;

    @Inject
    @Named( TestPropertyDefinitions.NAMED )
    private Properties testProperties;

    @Produces
    public UserManagerConfiguration getUserManagerConfiguration()
    {
        if ( umConfig == null )
        {
            umConfig =
                new DefaultUserManagerConfig(
                                              testProperties.getProperty( TestPropertyDefinitions.DATABASE_URL ),
                                              "admin@nowhere.com", "password", "Admin", "User" );
        }

        return umConfig;
    }

}
