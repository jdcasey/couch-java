package org.commonjava.web.user.rest;

import org.commonjava.auth.couch.data.UserAppDescription;
import org.commonjava.web.test.AbstractRESTCouchTest;
import org.commonjava.web.test.fixture.TestWarArchiveBuilder;
import org.commonjava.web.user.rest.fixture.RESTfulTestPropertiesProducer;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.spec.WebArchive;

public abstract class AbstractRESTfulUserManagerTest
    extends AbstractRESTCouchTest
{

    @Deployment
    public static WebArchive createTestWar()
    {
        TestWarArchiveBuilder builder =
            new TestWarArchiveBuilder( RESTfulTestPropertiesProducer.class );

        builder.withAllStandards();
        builder.withTestRESTApplication();
        builder.withTestUserManagerConfigProducer();
        builder.withApplication( new UserAppDescription() );
        builder.withExtraPackages( true, "org.commonjava" );

        return builder.build();
    }

    protected AbstractRESTfulUserManagerTest()
    {}

}
