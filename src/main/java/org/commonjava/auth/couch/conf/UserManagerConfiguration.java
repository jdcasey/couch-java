package org.commonjava.auth.couch.conf;

import org.commonjava.auth.couch.data.PasswordManager;
import org.commonjava.auth.couch.model.User;

public interface UserManagerConfiguration
{

    User createInitialAdminUser( final PasswordManager passwordManager );

    String getDatabaseUrl();

    String getLogicApplication();

}
