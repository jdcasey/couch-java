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

import static org.commonjava.auth.couch.util.IdUtils.namespaceId;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import org.commonjava.auth.couch.conf.UserManagerConfiguration;
import org.commonjava.auth.couch.data.UserViewRequest.View;
import org.commonjava.auth.couch.model.Permission;
import org.commonjava.auth.couch.model.Role;
import org.commonjava.auth.couch.model.User;
import org.commonjava.couch.db.CouchDBException;
import org.commonjava.couch.db.CouchManager;
import org.commonjava.couch.model.CouchDocRef;

public class UserDataManager
{

    @Inject
    private CouchManager couch;

    @Inject
    private UserManagerConfiguration config;

    public UserDataManager()
    {}

    public UserDataManager( final UserManagerConfiguration config, final CouchManager couch )
    {
        this.config = config;
        this.couch = couch;
    }

    public void install()
        throws UserDataException
    {
        try
        {
            couch.initialize( config.getDatabaseUrl(), config.getLogicApplication(),
                              UserViewRequest.APPLICATION_RESOURCE );
        }
        catch ( CouchDBException e )
        {
            throw new UserDataException(
                                         "Failed to initialize user-management database: %s (application: %s). Reason: %s",
                                         e, config.getDatabaseUrl(),
                                         UserViewRequest.APPLICATION_RESOURCE, e.getMessage() );
        }
    }

    public User getUser( final String username )
        throws UserDataException
    {
        try
        {
            return couch.getDocument( new CouchDocRef( namespaceId( User.NAMESPACE, username ) ),
                                      config.getDatabaseUrl(), User.class );
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
                                      config.getDatabaseUrl(), Permission.class );
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
                                      config.getDatabaseUrl(), Role.class );
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
            return new HashSet<Role>( couch.getViewListing( req, config.getDatabaseUrl(),
                                                            Role.class ) );
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
            return new HashSet<Permission>( couch.getViewListing( req, config.getDatabaseUrl(),
                                                                  Permission.class ) );
        }
        catch ( CouchDBException e )
        {
            throw new UserDataException( "Failed to get permissions for role: %s. Reason: %s", e,
                                         role.getName(), e.getMessage() );
        }
    }

    public void storePermission( final Permission perm )
        throws UserDataException
    {
        try
        {
            couch.store( perm, config.getDatabaseUrl(), true );
        }
        catch ( CouchDBException e )
        {
            throw new UserDataException( "Failed to store permission: %s. Reason: %s", e, perm,
                                         e.getMessage() );
        }
    }

    public void storeRole( final Role role )
        throws UserDataException
    {
        try
        {
            couch.store( role, config.getDatabaseUrl(), true );
        }
        catch ( CouchDBException e )
        {
            throw new UserDataException( "Failed to store role: %s. Reason: %s", e, role,
                                         e.getMessage() );
        }
    }

    public void storeUser( final User user )
        throws UserDataException
    {
        try
        {
            couch.store( user, config.getDatabaseUrl(), true );
        }
        catch ( CouchDBException e )
        {
            throw new UserDataException( "Failed to store user: %s. Reason: %s", e, user,
                                         e.getMessage() );
        }
    }

    protected final CouchManager getCouch()
    {
        return couch;
    }

    public Map<String, Permission> createPermissions( final String namespace, final String name,
                                                      final String... verbs )
        throws UserDataException
    {
        Map<String, Permission> result = new HashMap<String, Permission>();
        for ( String verb : verbs )
        {
            Permission perm = new Permission( namespace, name, verb );
            storePermission( perm );

            result.put( verb, perm );
        }

        return result;
    }

    public Role createRole( final String name, final Collection<Permission> permissions )
        throws UserDataException
    {
        Role role = new Role( name, permissions );
        storeRole( role );

        return role;
    }

    public Role createRole( final String name, final Permission... permissions )
        throws UserDataException
    {
        Role role = new Role( name, permissions );
        storeRole( role );

        return role;
    }

}
