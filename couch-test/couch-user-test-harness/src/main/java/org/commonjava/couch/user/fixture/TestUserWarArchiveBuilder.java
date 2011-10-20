package org.commonjava.couch.user.fixture;

import org.cjtest.fixture.TestAuthenticationFilter;
import org.cjtest.fixture.TestUserManagerConfigProducer;
import org.commonjava.auth.couch.change.RoleDeletionListener;
import org.commonjava.auth.couch.conf.UserManagerConfiguration;
import org.commonjava.auth.couch.data.UserDataManager;
import org.commonjava.auth.couch.inject.UserDataProvider;
import org.commonjava.auth.couch.model.User;
import org.commonjava.auth.shiro.couch.CouchRealm;
import org.commonjava.couch.user.web.test.AbstractUserRESTCouchTest;
import org.commonjava.web.test.fixture.TestWarArchiveBuilder;

public class TestUserWarArchiveBuilder
    extends TestWarArchiveBuilder
{

    private static final Package[] EXTRA_STD_PACKAGES = {
        UserDataManager.class.getPackage(),
        User.class.getPackage(),
        UserDataProvider.class.getPackage(),
        RoleDeletionListener.class.getPackage(),
        UserManagerConfiguration.class.getPackage(),
        CouchRealm.class.getPackage() };

    public TestUserWarArchiveBuilder( final Class<?> testPropertiesProducer )
    {
        super( testPropertiesProducer );
        withExtraClasses( AbstractUserRESTCouchTest.class );
    }

    public TestUserWarArchiveBuilder( final String warName, final Class<?> testPropertiesProducer )
    {
        super( warName, testPropertiesProducer );
        withExtraClasses( AbstractUserRESTCouchTest.class );
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

    @Override
    public TestWarArchiveBuilder withStandardPackages()
    {
        super.withStandardPackages();

        war.addPackages( true, EXTRA_STD_PACKAGES );

        return this;
    }

    @Override
    public TestWarArchiveBuilder withAllStandards()
    {
        super.withAllStandards();
        return withStandardAuthentication();
    }

}
