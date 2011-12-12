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
package org.commonjava.auth.couch.data;

import static org.commonjava.couch.util.IdUtils.namespaceId;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.commonjava.auth.couch.change.event.PermissionUpdateEvent;
import org.commonjava.auth.couch.change.event.RoleUpdateEvent;
import org.commonjava.auth.couch.change.event.UpdateType;
import org.commonjava.auth.couch.change.event.UserManagerDeleteEvent;
import org.commonjava.auth.couch.change.event.UserUpdateEvent;
import org.commonjava.auth.couch.conf.UserManagerConfiguration;
import org.commonjava.auth.couch.data.UserAppDescription.View;
import org.commonjava.auth.couch.inject.UserData;
import org.commonjava.auth.couch.model.PermissionDoc;
import org.commonjava.auth.couch.model.RoleDoc;
import org.commonjava.auth.couch.model.UserDoc;
import org.commonjava.couch.db.CouchDBException;
import org.commonjava.couch.db.CouchManager;
import org.commonjava.couch.db.model.ViewRequest;
import org.commonjava.couch.model.CouchDocRef;
import org.commonjava.couch.rbac.Permission;
import org.commonjava.couch.rbac.Role;
import org.commonjava.couch.rbac.User;

@Singleton
public class UserDataManager
{

    @Inject
    @UserData
    private CouchManager couch;

    @Inject
    private UserManagerConfiguration config;

    @Inject
    private PasswordManager passwordManager;

    @Inject
    private Event<UserUpdateEvent> userEvent;

    @Inject
    private Event<RoleUpdateEvent> roleEvent;

    @Inject
    private Event<PermissionUpdateEvent> permissionEvent;

    @Inject
    private Event<UserManagerDeleteEvent> deleteEvent;

    public UserDataManager()
    {
    }

    public UserDataManager( final UserManagerConfiguration config, final PasswordManager passwordManager,
                            final CouchManager couch )
    {
        this.config = config;
        this.passwordManager = passwordManager;
        this.couch = couch;
    }

    public void install()
        throws UserDataException
    {
        final UserAppDescription description = new UserAppDescription();
        try
        {
            couch.initialize( description );
        }
        catch ( final CouchDBException e )
        {
            throw new UserDataException( "Failed to initialize user-management database (application: %s). Reason: %s",
                                         e, description, e.getMessage() );
        }
    }

    public void setupAdminInformation()
        throws UserDataException
    {
        storePermission( new Permission( Permission.WILDCARD ) );
        storePermission( new Permission( Permission.NAMESPACE, Permission.ADMIN ) );
        storePermission( new Permission( Role.NAMESPACE, Permission.ADMIN ) );
        storePermission( new Permission( User.NAMESPACE, Permission.ADMIN ) );

        final Role role = new Role( Role.ADMIN );
        role.addPermission( Permission.WILDCARD );

        storeRole( role, true );

        final User user = config.createInitialAdminUser( passwordManager );
        user.addRole( Role.ADMIN );

        storeUser( user, true );
    }

    public User getUser( final String username )
        throws UserDataException
    {
        try
        {
            return couch.getDocument( new CouchDocRef( namespaceId( User.NAMESPACE, username ) ), UserDoc.class )
                        .toUser();
        }
        catch ( final CouchDBException e )
        {
            throw new UserDataException( "Failed to retrieve user: %s. Reason: %s", e, username, e.getMessage() );
        }
    }

    public Permission getPermission( final String name )
        throws UserDataException
    {
        try
        {
            return couch.getDocument( new CouchDocRef( namespaceId( Permission.NAMESPACE, name ) ), PermissionDoc.class )
                        .toPermission();
        }
        catch ( final CouchDBException e )
        {
            throw new UserDataException( "Failed to retrieve permission: %s. Reason: %s", e, name, e.getMessage() );
        }
    }

    public Role getRole( final String name )
        throws UserDataException
    {
        try
        {
            return couch.getDocument( new CouchDocRef( namespaceId( Role.NAMESPACE, name ) ), RoleDoc.class )
                        .toRole();
        }
        catch ( final CouchDBException e )
        {
            throw new UserDataException( "Failed to retrieve role: %s. Reason: %s", e, name, e.getMessage() );
        }
    }

    public Set<Role> getRoles( final User user )
        throws UserDataException
    {
        final UserViewRequest req = new UserViewRequest( config, View.USER_ROLES );
        try
        {
            return RoleDoc.toRoleSet( couch.getViewListing( req, RoleDoc.class ) );
        }
        catch ( final CouchDBException e )
        {
            throw new UserDataException( "Failed to get roles for user: %s. Reason: %s", e, user.getUsername(),
                                         e.getMessage() );
        }
    }

    public Set<Permission> getPermissions( final Role role )
        throws UserDataException
    {
        final UserViewRequest req = new UserViewRequest( config, View.ROLE_PERMISSIONS );
        try
        {
            return PermissionDoc.toPermissionSet( couch.getViewListing( req, PermissionDoc.class ) );
        }
        catch ( final CouchDBException e )
        {
            throw new UserDataException( "Failed to get permissions for role: %s. Reason: %s", e, role.getName(),
                                         e.getMessage() );
        }
    }

    public void storePermissions( final Collection<Permission> perms )
        throws UserDataException
    {
        try
        {
            couch.store( PermissionDoc.toDocuments( perms ), true, false );
            firePermissionEvent( UpdateType.ADD, perms );
        }
        catch ( final CouchDBException e )
        {
            throw new UserDataException( "Failed to store %d permissions. Error: %s", e, perms.size(), e.getMessage() );
        }
    }

    public boolean storePermission( final Permission perm )
        throws UserDataException
    {
        try
        {
            final boolean result = couch.store( new PermissionDoc( perm ), true );
            firePermissionEvent( UpdateType.ADD, perm );

            return result;
        }
        catch ( final CouchDBException e )
        {
            throw new UserDataException( "Failed to store permission: %s. Reason: %s", e, perm, e.getMessage() );
        }
    }

    public void storeRoles( final Collection<Role> roles )
        throws UserDataException
    {
        try
        {
            couch.store( RoleDoc.toDocuments( roles ), false, false );
            fireRoleEvent( UpdateType.ADD_OR_UPDATE, roles );
        }
        catch ( final CouchDBException e )
        {
            throw new UserDataException( "Failed to update %d roles. Error: %s", e, roles.size(), e.getMessage() );
        }
    }

    public boolean storeRole( final Role role )
        throws UserDataException
    {
        return storeRole( role, false );
    }

    public boolean storeRole( final Role role, final boolean skipIfExists )
        throws UserDataException
    {
        try
        {
            final boolean result = couch.store( new RoleDoc( role ), skipIfExists );
            fireRoleEvent( skipIfExists ? UpdateType.ADD : UpdateType.ADD_OR_UPDATE, role );
            return result;
        }
        catch ( final CouchDBException e )
        {
            throw new UserDataException( "Failed to store role: %s. Reason: %s", e, role, e.getMessage() );
        }
    }

    public void storeUsers( final Collection<User> users )
        throws UserDataException
    {
        try
        {
            couch.store( UserDoc.toDocuments( users ), false, false );
            fireUserEvent( UpdateType.ADD_OR_UPDATE, users );
        }
        catch ( final CouchDBException e )
        {
            throw new UserDataException( "Failed to update %d users. Error: %s", e, users.size(), e.getMessage() );
        }
    }

    public boolean storeUser( final User user )
        throws UserDataException
    {
        return storeUser( user, false );
    }

    public boolean storeUser( final User user, final boolean skipIfExists )
        throws UserDataException
    {
        try
        {
            final boolean result = couch.store( new UserDoc( user ), skipIfExists );
            fireUserEvent( skipIfExists ? UpdateType.ADD : UpdateType.ADD_OR_UPDATE, user );
            return result;
        }
        catch ( final CouchDBException e )
        {
            throw new UserDataException( "Failed to store user: %s. Reason: %s", e, user, e.getMessage() );
        }
    }

    public Map<String, Permission> createPermissions( final String namespace, final String name, final String... verbs )
        throws UserDataException
    {
        final Map<String, Permission> result = new HashMap<String, Permission>();
        for ( final String verb : verbs )
        {
            Permission perm = new Permission( namespace, name, verb );
            if ( !storePermission( perm ) )
            {
                perm = getPermission( perm.getName() );
            }

            result.put( verb, perm );
        }

        return result;
    }

    public Role createRole( final String name, final Collection<Permission> permissions )
        throws UserDataException
    {
        Role role = new Role( name, permissions );
        if ( !storeRole( role, true ) )
        {
            role = getRole( name );
        }

        return role;
    }

    public Role createRole( final String name, final Permission... permissions )
        throws UserDataException
    {
        Role role = new Role( name, permissions );
        if ( !storeRole( role, true ) )
        {
            role = getRole( name );
        }

        return role;
    }

    public Set<User> getAllUsers()
        throws UserDataException
    {
        try
        {
            return UserDoc.toUserSet( couch.getViewListing( new UserViewRequest( config, View.ALL_USERS ),
                                                            UserDoc.class ) );
        }
        catch ( final CouchDBException e )
        {
            throw new UserDataException( "Failed to retrieve full listing of users: %s", e, e.getMessage() );
        }
    }

    public void deleteUser( final String name )
        throws UserDataException
    {
        try
        {
            couch.delete( new CouchDocRef( namespaceId( User.NAMESPACE, name ) ) );
            fireDeleteEvent( UserManagerDeleteEvent.Type.USER, name );
        }
        catch ( final CouchDBException e )
        {
            throw new UserDataException( "Failed to delete user: %s. Reason: %s", e, name, e.getMessage() );
        }
    }

    public Set<Role> getAllRoles()
        throws UserDataException
    {
        try
        {
            return RoleDoc.toRoleSet( couch.getViewListing( new UserViewRequest( config, View.ALL_ROLES ),
                                                            RoleDoc.class ) );
        }
        catch ( final CouchDBException e )
        {
            throw new UserDataException( "Failed to retrieve full listing of roles: %s", e, e.getMessage() );
        }
    }

    public void deleteRole( final String name )
        throws UserDataException
    {
        try
        {
            couch.delete( new CouchDocRef( namespaceId( Role.NAMESPACE, name ) ) );
            fireDeleteEvent( UserManagerDeleteEvent.Type.ROLE, name );
        }
        catch ( final CouchDBException e )
        {
            throw new UserDataException( "Failed to delete role: %s. Reason: %s", e, name, e.getMessage() );
        }
    }

    public Set<Permission> getAllPermissions()
        throws UserDataException
    {
        try
        {
            return PermissionDoc.toPermissionSet( couch.getViewListing( new UserViewRequest( config,
                                                                                             View.ALL_PERMISSIONS ),
                                                                        PermissionDoc.class ) );
        }
        catch ( final CouchDBException e )
        {
            throw new UserDataException( "Failed to retrieve full listing of permission: %s", e, e.getMessage() );
        }
    }

    public void deletePermission( final String name )
        throws UserDataException
    {
        try
        {
            couch.delete( new CouchDocRef( namespaceId( Permission.NAMESPACE, name ) ) );
            fireDeleteEvent( UserManagerDeleteEvent.Type.PERMISSION, name );
        }
        catch ( final CouchDBException e )
        {
            throw new UserDataException( "Failed to delete permission: %s. Reason: %s", e, name, e.getMessage() );
        }
    }

    public Set<User> getUsersForRole( final String role )
        throws UserDataException
    {
        try
        {
            final UserViewRequest req = new UserViewRequest( config, View.ROLE_USERS );
            req.setParameter( ViewRequest.KEY, role );

            return UserDoc.toUserSet( couch.getViewListing( req, UserDoc.class ) );
        }
        catch ( final CouchDBException e )
        {
            throw new UserDataException( "Failed to lookup users belonging to role: %s. Reason: %s", e, role,
                                         e.getMessage() );
        }
    }

    public Set<Role> getRolesForPermission( final String permission )
        throws UserDataException
    {
        try
        {
            final UserViewRequest req = new UserViewRequest( config, View.PERMISSION_ROLES );
            req.setParameter( ViewRequest.KEY, permission );

            return RoleDoc.toRoleSet( couch.getViewListing( req, RoleDoc.class ) );
        }
        catch ( final CouchDBException e )
        {
            throw new UserDataException( "Failed to lookup roles granting permission: %s. Reason: %s", e, permission,
                                         e.getMessage() );
        }
    }

    private void fireUserEvent( final UpdateType type, final Collection<User> users )
    {
        if ( userEvent != null )
        {
            userEvent.fire( new UserUpdateEvent( type, users ) );
        }
    }

    private void fireUserEvent( final UpdateType type, final User... users )
    {
        if ( userEvent != null )
        {
            userEvent.fire( new UserUpdateEvent( type, users ) );
        }
    }

    private void fireRoleEvent( final UpdateType type, final Collection<Role> roles )
    {
        if ( roleEvent != null )
        {
            roleEvent.fire( new RoleUpdateEvent( type, roles ) );
        }
    }

    private void fireRoleEvent( final UpdateType type, final Role... roles )
    {
        if ( roleEvent != null )
        {
            roleEvent.fire( new RoleUpdateEvent( type, roles ) );
        }
    }

    private void firePermissionEvent( final UpdateType type, final Collection<Permission> perms )
    {
        if ( permissionEvent != null )
        {
            permissionEvent.fire( new PermissionUpdateEvent( type, perms ) );
        }
    }

    private void firePermissionEvent( final UpdateType type, final Permission... perms )
    {
        if ( permissionEvent != null )
        {
            permissionEvent.fire( new PermissionUpdateEvent( type, perms ) );
        }
    }

    private void fireDeleteEvent( final UserManagerDeleteEvent.Type type, final String... names )
    {
        if ( deleteEvent != null )
        {
            deleteEvent.fire( new UserManagerDeleteEvent( type, names ) );
        }
    }

}
