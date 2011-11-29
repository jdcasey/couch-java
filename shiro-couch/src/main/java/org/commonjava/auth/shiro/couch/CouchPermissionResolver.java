/*******************************************************************************
 * Copyright 2011 John Casey
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package org.commonjava.auth.shiro.couch;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.Permission;
import org.apache.shiro.authz.permission.PermissionResolver;
import org.apache.shiro.authz.permission.RolePermissionResolver;
import org.commonjava.auth.couch.data.UserDataException;
import org.commonjava.auth.couch.data.UserDataManager;
import org.commonjava.auth.couch.model.Role;
import org.commonjava.auth.shiro.couch.model.ShiroPermission;
import org.commonjava.util.logging.Logger;

@Singleton
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
        // logger.info( "Resolving permission: %s", permissionName );

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
        // logger.info( "Resolving permissions for role: %s", roleName );

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
