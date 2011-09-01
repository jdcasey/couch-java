package org.commonjava.auth.shiro.couch;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;

import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.Permission;
import org.apache.shiro.authz.permission.PermissionResolver;
import org.apache.shiro.authz.permission.RolePermissionResolver;
import org.commonjava.auth.couch.data.UserDataException;
import org.commonjava.auth.couch.data.UserDataManager;
import org.commonjava.auth.couch.model.Role;
import org.commonjava.auth.shiro.couch.model.ShiroPermission;
import org.commonjava.util.logging.Logger;

public class CouchPermissionResolver
    implements PermissionResolver, RolePermissionResolver
{

    private final Logger logger = new Logger( getClass() );

    @Inject
    private UserDataManager dataManager;

    @Override
    public Permission resolvePermission( final String permissionName )
    {
        try
        {
            return (ShiroPermission) dataManager.getPermission( permissionName );
        }
        catch ( UserDataException e )
        {
            logger.error( "Failed to retrieve permission: %s. Reason: %s", e, permissionName,
                          e.getMessage() );

            throw new AuthorizationException(
                                              "Cannot retrieve permission. System configuration is invalid." );
        }
    }

    @Override
    public Collection<Permission> resolvePermissionsInRole( final String roleName )
    {
        final Set<Permission> perms = new HashSet<Permission>();

        Role role;
        try
        {
            role = dataManager.getRole( roleName );
        }
        catch ( UserDataException e )
        {
            logger.error( "Failed to retrieve role: %s. Reason: %s", e, roleName, e.getMessage() );

            throw new AuthorizationException(
                                              "Cannot retrieve role. System configuration is invalid." );
        }

        if ( role.getPermissions() != null )
        {
            Set<org.commonjava.auth.couch.model.Permission> permissions;
            try
            {
                permissions = dataManager.getPermissions( role );
            }
            catch ( UserDataException e )
            {
                logger.error( "Failed to retrieve permissions for role: %s. Reason: %s", e,
                              roleName, e.getMessage() );

                throw new AuthorizationException(
                                                  "Cannot retrieve permissions for role. System configuration is invalid." );
            }

            for ( final org.commonjava.auth.couch.model.Permission perm : permissions )
            {
                perms.add( (ShiroPermission) perm );
            }
        }

        return perms;
    }

}
