package org.commonjava.web.user.rest.fixture;

import java.util.Properties;

import javax.enterprise.inject.Produces;
import javax.inject.Named;

import org.commonjava.web.test.fixture.TestPropertyDefinitions;

public class RESTfulTestPropertiesProducer
{

    @Produces
    @Named( "testProperties" )
    public Properties createTestProperties()
    {
        Properties props = new Properties();
        props.setProperty( TestPropertyDefinitions.DATABASE_URL,
                           "http://developer.commonjava.org/db/test-user-manager" );

        return props;
    }

}
