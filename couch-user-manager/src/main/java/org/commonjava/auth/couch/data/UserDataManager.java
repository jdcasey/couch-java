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
package org.commonjava.auth.couch.data;

import static org.commonjava.couch.util.IdUtils.namespaceId;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
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
import org.commonjava.auth.couch.inject.UserDatabase;
import org.commonjava.auth.couch.model.Permission;
import org.commonjava.auth.couch.model.Role;
import org.commonjava.auth.couch.model.User;
import org.commonjava.couch.db.CouchDBException;
import org.commonjava.couch.db.CouchManager;
import org.commonjava.couch.db.model.ViewRequest;
import org.commonjava.couch.model.CouchDocRef;

@Singleton
public class UserDataManager
{

    @Inject
    @UserDatabase
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
    {}

    public UserDataManager( final UserManagerConfiguration config,
                            final PasswordManager passwordManager, final CouchManager couch )
    {
        this.config = config;
        this.passwordManager = passwordManager;
        this.couch = couch;
    }

    public void install()
        throws UserDataException
    {
        UserAppDescription description = new UserAppDescription();
        try
        {
            couch.initialize( description );
        }
        catch ( CouchDBException e )
        {
            throw new UserDataException(
                                         "Failed to initialize user-management database (application: %s). Reason: %s",
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

        Role role = new Role( Role.ADMIN );
        role.addPermission( Permission.WILDCARD );

        storeRole( role, true );

        User user = config.createInitialAdminUser( passwordManager );
        user.addRole( Role.ADMIN );

        storeUser( user, true );
    }

    public User getUser( final String username )
        throws UserDataException
    {
        try
        {
            return couch.getDocument( new CouchDocRef( namespaceId( User.NAMESPACE, username ) ),
                                      User.class );
        }
        catch ( CouchDBException e )
        {
            throw new UserDataException( "Failed to retrieve user: %s. Reason: %s", e, username,
                                         e.getMessage() );
        }
    }

    public Permission getPermission( final String name )
        throws UserDataException
    {
        try
        {
            return couch.getDocument( new CouchDocRef( namespaceId( Permission.NAMESPACE, name ) ),
                                      Permission.class );
        }
        catch ( CouchDBException e )
        {
            throw new UserDataException( "Failed to retrieve permission: %s. Reason: %s", e, name,
                                         e.getMessage() );
        }
    }

    public Role getRole( final String name )
        throws UserDataException
    {
        try
        {
            return couch.getDocument( new CouchDocRef( namespaceId( Role.NAMESPACE, name ) ),
                                      Role.class );
        }
        catch ( CouchDBException e )
        {
            throw new UserDataException( "Failed to retrieve role: %s. Reason: %s", e, name,
                                         e.getMessage() );
        }
    }

    public Set<Role> getRoles( final User user )
        throws UserDataException
    {
        UserViewRequest req = new UserViewRequest( config, View.USER_ROLES );
        try
        {
            return new HashSet<Role>( couch.getViewListing( req, Role.class ) );
        }
        catch ( CouchDBException e )
        {
            throw new UserDataException( "Failed to get roles for user: %s. Reason: %s", e,
                                         user.getUsername(), e.getMessage() );
        }
    }

    public Set<Permission> getPermissions( final Role role )
        throws UserDataException
    {
        UserViewRequest req = new UserViewRequest( config, View.ROLE_PERMISSIONS );
        try
        {
            return new HashSet<Permission>( couch.getViewListing( req, Permission.class ) );
        }
        catch ( CouchDBException e )
        {
            throw new UserDataException( "Failed to get permissions for role: %s. Reason: %s", e,
                                         role.getName(), e.getMessage() );
        }
    }

    public void storePermissions( final Collection<Permission> perms )
        throws UserDataException
    {
        try
        {
            couch.store( perms, true, false );
            firePermissionEvent( UpdateType.ADD, perms );
        }
        catch ( CouchDBException e )
        {
            throw new UserDataException( "Failed to store %d permissions. Error: %s", e,
                                         perms.size(), e.getMessage() );
        }
    }

    public boolean storePermission( final Permission perm )
        throws UserDataException
    {
        try
        {
            boolean result = couch.store( perm, true );
            firePermissionEvent( UpdateType.ADD, perm );

            return result;
        }
        catch ( CouchDBException e )
        {
            throw new UserDataException( "Failed to store permission: %s. Reason: %s", e, perm,
                                         e.getMessage() );
        }
    }

    public void storeRoles( final Collection<Role> roles )
        throws UserDataException
    {
        try
        {
            couch.store( roles, false, false );
            fireRoleEvent( UpdateType.ADD_OR_UPDATE, roles );
        }
        catch ( CouchDBException e )
        {
            throw new UserDataException( "Failed to update %d roles. Error: %s", e, roles.size(),
                                         e.getMessage() );
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
            boolean result = couch.store( role, skipIfExists );
            fireRoleEvent( skipIfExists ? UpdateType.ADD : UpdateType.ADD_OR_UPDATE, role );
            return result;
        }
        catch ( CouchDBException e )
        {
            throw new UserDataException( "Failed to store role: %s. Reason: %s", e, role,
                                         e.getMessage() );
        }
    }

    public void storeUsers( final Collection<User> users )
        throws UserDataException
    {
        try
        {
            couch.store( users, false, false );
            fireUserEvent( UpdateType.ADD_OR_UPDATE, users );
        }
        catch ( CouchDBException e )
        {
            throw new UserDataException( "Failed to update %d users. Error: %s", e, users.size(),
                                         e.getMessage() );
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
            boolean result = couch.store( user, skipIfExists );
            fireUserEvent( skipIfExists ? UpdateType.ADD : UpdateType.ADD_OR_UPDATE, user );
            return result;
        }
        catch ( CouchDBException e )
        {
            throw new UserDataException( "Failed to store user: %s. Reason: %s", e, user,
                                         e.getMessage() );
        }
    }

    public Map<String, Permission> createPermissions( final String namespace, final String name,
                                                      final String... verbs )
        throws UserDataException
    {
        Map<String, Permission> result = new HashMap<String, Permission>();
        for ( String verb : verbs )
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
            List<User> users =
                couch.getViewListing( new UserViewRequest( config, View.ALL_USERS ), User.class );

            return new HashSet<User>( users );
        }
        catch ( CouchDBException e )
        {
            throw new UserDataException( "Failed to retrieve full listing of users: %s", e,
                                         e.getMessage() );
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
        catch ( CouchDBException e )
        {
            throw new UserDataException( "Failed to delete user: %s. Reason: %s", e, name,
                                         e.getMessage() );
        }
    }

    public Set<Role> getAllRoles()
        throws UserDataException
    {
        try
        {
            List<Role> roles =
                couch.getViewListing( new UserViewRequest( config, View.ALL_ROLES ), Role.class );

            return new HashSet<Role>( roles );
        }
        catch ( CouchDBException e )
        {
            throw new UserDataException( "Failed to retrieve full listing of roles: %s", e,
                                         e.getMessage() );
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
        catch ( CouchDBException e )
        {
            throw new UserDataException( "Failed to delete role: %s. Reason: %s", e, name,
                                         e.getMessage() );
        }
    }

    public Set<Permission> getAllPermissions()
        throws UserDataException
    {
        try
        {
            List<Permission> permissions =
                couch.getViewListing( new UserViewRequest( config, View.ALL_PERMISSIONS ),
                                      Permission.class );

            return new HashSet<Permission>( permissions );
        }
        catch ( CouchDBException e )
        {
            throw new UserDataException( "Failed to retrieve full listing of permission: %s", e,
                                         e.getMessage() );
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
        catch ( CouchDBException e )
        {
            throw new UserDataException( "Failed to delete permission: %s. Reason: %s", e, name,
                                         e.getMessage() );
        }
    }

    public Set<User> getUsersForRole( final String role )
        throws UserDataException
    {
        try
        {
            UserViewRequest req = new UserViewRequest( config, View.ROLE_USERS );
            req.setParameter( ViewRequest.KEY, role );

            List<User> users = couch.getViewListing( req, User.class );

            return new HashSet<User>( users );
        }
        catch ( CouchDBException e )
        {
            throw new UserDataException(
                                         "Failed to lookup users belonging to role: %s. Reason: %s",
                                         e, role, e.getMessage() );
        }
    }

    public Set<Role> getRolesForPermission( final String permission )
        throws UserDataException
    {
        try
        {
            UserViewRequest req = new UserViewRequest( config, View.PERMISSION_ROLES );
            req.setParameter( ViewRequest.KEY, permission );

            List<Role> roles = couch.getViewListing( req, Role.class );

            return new HashSet<Role>( roles );
        }
        catch ( CouchDBException e )
        {
            throw new UserDataException(
                                         "Failed to lookup roles granting permission: %s. Reason: %s",
                                         e, permission, e.getMessage() );
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
