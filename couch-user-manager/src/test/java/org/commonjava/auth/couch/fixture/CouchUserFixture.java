package org.commonjava.auth.couch.fixture;

import javax.enterprise.inject.Default;
import javax.enterprise.inject.Produces;
import javax.inject.Singleton;

import org.cjtest.fixture.CouchFixture;
import org.commonjava.auth.couch.conf.DefaultUserManagerConfig;
import org.commonjava.auth.couch.conf.UserManagerConfiguration;
import org.commonjava.auth.couch.data.PasswordManager;
import org.commonjava.auth.couch.data.UserDataManager;
import org.commonjava.auth.couch.inject.UserDatabase;
import org.commonjava.couch.conf.CouchDBConfiguration;
import org.jboss.weld.environment.se.WeldContainer;

@UserDatabase
public class CouchUserFixture
    extends CouchFixture
{

    public static final String DB_URL = "http://localhost:5984/test-user-db";

    private final UserDataManager userDataManager;

    private final boolean setupAdminInfo;

    private final UserManagerConfiguration userConfig;

    private final PasswordManager passwordManager;

    public CouchUserFixture( final WeldContainer weld )
    {
        super( weld, getFixtureQualifiers( CouchUserFixture.class ) );
        userConfig = weld.instance().select( UserManagerConfiguration.class ).get();
        passwordManager = weld.instance().select( PasswordManager.class ).get();
        userDataManager = weld.instance().select( UserDataManager.class ).get();
        setupAdminInfo = true;
    }

    public CouchUserFixture()
    {
        super( DB_URL );
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
    public static final class UserFixtureConfigProvider
    {
        private UserManagerConfiguration umConfig;

        @Produces
        public UserManagerConfiguration getUserManagerConfig()
        {
            if ( umConfig == null )
            {
                DefaultUserManagerConfig c = new DefaultUserManagerConfig();
                c.setAdminPassword( "password" );
                c.setDatabaseUrl( DB_URL );
                umConfig = c;
            }

            return umConfig;
        }

        @Produces
        @UserDatabase
        @Default
        public CouchDBConfiguration getConfig()
        {
            return getUserManagerConfig().getUserDatabaseConfig();
        }
    }

}
