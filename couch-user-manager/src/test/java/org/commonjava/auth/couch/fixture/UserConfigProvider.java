package org.commonjava.auth.couch.fixture;

import javax.enterprise.inject.Default;
import javax.enterprise.inject.Produces;
import javax.inject.Singleton;

import org.commonjava.auth.couch.conf.DefaultUserManagerConfig;
import org.commonjava.auth.couch.conf.UserManagerConfiguration;
import org.commonjava.auth.couch.inject.UserData;
import org.commonjava.couch.conf.CouchDBConfiguration;

@Singleton
public class UserConfigProvider
{

    public static final String DB_URL = "http://localhost:5984/test-user-db";

    private UserManagerConfiguration umConfig;

    @Produces
    public UserManagerConfiguration getUserManagerConfig()
    {
        if ( umConfig == null )
        {
            final DefaultUserManagerConfig c = new DefaultUserManagerConfig();
            c.setAdminPassword( "password" );
            c.setDatabaseUrl( DB_URL );
            umConfig = c;
        }

        return umConfig;
    }

    @Produces
    @UserData
    @Default
    public CouchDBConfiguration getConfig()
    {
        return getUserManagerConfig().getDatabaseConfig();
    }
}
