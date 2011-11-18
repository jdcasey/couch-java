package org.commonjava.auth.couch.fixture;

import org.cjtest.fixture.CouchFixture;
import org.commonjava.auth.couch.conf.DefaultUserManagerConfig;
import org.commonjava.auth.couch.conf.UserManagerConfiguration;
import org.commonjava.auth.couch.data.PasswordManager;
import org.commonjava.auth.couch.data.UserDataManager;
import org.commonjava.auth.couch.inject.UserData;

@UserData
public class CouchUserFixture
    extends CouchFixture
{

    private final UserDataManager userDataManager;

    private final boolean setupAdminInfo;

    private final UserManagerConfiguration userConfig;

    private final PasswordManager passwordManager;

    public CouchUserFixture()
    {
        super( UserConfigProvider.DB_URL );
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

}
