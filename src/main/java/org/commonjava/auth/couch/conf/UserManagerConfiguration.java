package org.commonjava.auth.couch.conf;

import org.commonjava.auth.couch.data.PasswordManager;
import org.commonjava.auth.couch.model.Permission;
import org.commonjava.auth.couch.model.Role;
import org.commonjava.auth.couch.model.User;
import org.commonjava.auth.couch.model.factory.PermissionCreator;
import org.commonjava.auth.couch.model.factory.RoleCreator;
import org.commonjava.auth.couch.model.factory.UserCreator;

public interface UserManagerConfiguration
{

    User createInitialAdminUser( final PasswordManager passwordManager );

    String getDatabaseUrl();

    String getLogicApplication();

    UserCreator<?> getUserCreator();

    RoleCreator<?> getRoleCreator();

    PermissionCreator<?> getPermissionCreator();

    <U extends User> Class<U> getUserClass();

    <R extends Role> Class<R> getRoleClass();

    <P extends Permission> Class<P> getPermissionClass();

}
