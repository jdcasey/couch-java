package org.commonjava.web.user.rest.fixture;

import javax.enterprise.inject.Produces;

import org.commonjava.auth.couch.conf.DefaultUserManagerConfig;
import org.commonjava.auth.couch.conf.UserManagerConfiguration;
import org.commonjava.auth.couch.data.UserViewRequest;

public class TestProducers
{

    // private static CouchManager couch;

    private static UserManagerConfiguration umConfig;

    // private static CouchRealm realm;
    //
    // @Inject
    // private UserDataManager userDataManager;

    // @Produces
    // public CouchManager getCouchManager()
    // {
    // if ( couch == null )
    // {
    // couch = new CouchManager();
    // }
    //
    // return couch;
    // }

    // @Produces
    // public CouchRealm getCouchRealm()
    // {
    // if ( realm == null )
    // {
    // realm =
    // new CouchRealm( userDataManager, new CouchPermissionResolver( userDataManager ) );
    // }
    //
    // return realm;
    // }

    @Produces
    public UserManagerConfiguration getUserManagerConfiguration()
    {
        if ( umConfig == null )
        {
            umConfig =
                new DefaultUserManagerConfig(
                                              "http://developer.commonjava.org/db/test-user-manager",
                                              UserViewRequest.APPLICATION_RESOURCE,
                                              "admin@nowhere.com", "password", "Admin", "User" );
        }

        return umConfig;
    }

}
