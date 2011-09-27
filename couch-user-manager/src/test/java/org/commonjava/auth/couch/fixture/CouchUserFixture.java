package org.commonjava.auth.couch.fixture;

import javax.enterprise.inject.Produces;
import javax.inject.Singleton;

import org.cjtest.fixture.CouchFixture;
import org.commonjava.auth.couch.conf.DefaultUserManagerConfig;
import org.commonjava.auth.couch.conf.UserManagerConfiguration;
import org.commonjava.auth.couch.data.PasswordManager;
import org.commonjava.auth.couch.data.UserDataManager;
import org.jboss.weld.environment.se.WeldContainer;

public class CouchUserFixture
    extends CouchFixture
{

    private final UserDataManager userDataManager;

    private final boolean setupAdminInfo;

    private final UserManagerConfiguration userConfig;

    private final PasswordManager passwordManager;

    public CouchUserFixture( final WeldContainer weld )
    {
        super( weld );
        userConfig = weld.instance().select( UserManagerConfiguration.class ).get();
        passwordManager = weld.instance().select( PasswordManager.class ).get();
        userDataManager = weld.instance().select( UserDataManager.class ).get();
        setupAdminInfo = true;
    }

    public CouchUserFixture()
    {
        this.userConfig = new DefaultUserManagerConfig();
        this.passwordManager = new PasswordManager();
        this.userDataManager = new UserDataManager( userConfig, passwordManager, getCouchManager() );

        setupAdminInfo = false;
    }

    public UserDataManager getUserDataManager()
    {
        return userDataManager;
    }

    @Override
    protected void before()
        throws Throwable
    {
        super.before();

        userDataManager.install();

        if ( setupAdminInfo )
        {
            userDataManager.setupAdminInformation();
        }
    }

    @Singleton
    public static final class ConfigProvider
    {
        private UserManagerConfiguration umConfig;

        @Produces
        public UserManagerConfiguration getUserManagerConfig()
        {
            if ( umConfig == null )
            {
                DefaultUserManagerConfig c = new DefaultUserManagerConfig();
                c.setAdminPassword( "password" );
                umConfig = c;
            }

            return umConfig;
        }
    }

}
