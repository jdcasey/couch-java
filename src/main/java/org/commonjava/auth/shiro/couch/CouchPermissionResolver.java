/*******************************************************************************
 * Copyright (C) 2011  John Casey
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public
 * License along with this program.  If not, see 
 * <http://www.gnu.org/licenses/>.
 ******************************************************************************/
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

    CouchPermissionResolver()
    {}

    public CouchPermissionResolver( final UserDataManager dataManager )
    {
        this.dataManager = dataManager;
    }

    @Override
    public Permission resolvePermission( final String permissionName )
    {
        try
        {
            org.commonjava.auth.couch.model.Permission perm =
                dataManager.getPermission( permissionName );
            if ( perm == null )
            {
                throw new AuthorizationException( "No such permission: " + permissionName );
            }

            return new ShiroPermission( perm );
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

        if ( role == null )
        {
            throw new AuthorizationException( "No such role: " + roleName );
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

            if ( permissions != null )
            {
                for ( final org.commonjava.auth.couch.model.Permission perm : permissions )
                {
                    perms.add( new ShiroPermission( perm ) );
                }
            }
        }

        return perms;
    }

}
