package org.commonjava.auth.couch.data;

import static org.commonjava.auth.couch.util.IdUtils.namespaceId;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;

import org.commonjava.auth.couch.conf.UserManagerConfiguration;
import org.commonjava.auth.couch.data.UserViewRequest.View;
import org.commonjava.auth.couch.model.Permission;
import org.commonjava.auth.couch.model.Role;
import org.commonjava.auth.couch.model.User;
import org.commonjava.couch.db.CouchDBException;
import org.commonjava.couch.db.CouchManager;
import org.commonjava.couch.model.CouchApp;
import org.commonjava.couch.model.CouchDocRef;
import org.commonjava.couch.model.io.CouchAppReader;

public class UserDataManager
{

    @Inject
    private CouchManager couch;

    @Inject
    private UserManagerConfiguration config;

    @Inject
    private CouchAppReader appReader;

    UserDataManager()
    {}

    public UserDataManager( final UserManagerConfiguration config, final CouchManager couch,
                            final CouchAppReader appReader )
    {
        this.config = config;
        this.couch = couch;
        this.appReader = appReader;
    }

    public void install()
        throws UserDataException
    {
        CouchApp app;
        try
        {
            app =
                appReader.readAppDefinition( config.getLogicApplication(),
                                             UserViewRequest.APPLICATION_RESOURCE );
        }
        catch ( IOException e )
        {
            throw new UserDataException(
                                         "Failed to retrieve application definition: %s. Reason: %s",
                                         e, UserViewRequest.APPLICATION_RESOURCE, e.getMessage() );
        }

        try
        {
            if ( !couch.dbExists( config.getDatabaseUrl() ) )
            {
                couch.createDatabase( config.getDatabaseUrl() );
            }
        }
        catch ( CouchDBException e )
        {
            throw new UserDataException( "Failed to create database: %s.\nReason: %s", e,
                                         config.getDatabaseUrl(), e.getMessage() );
        }

        try
        {
            if ( !couch.appExists( config.getDatabaseUrl(), config.getLogicApplication() ) )
            {
                couch.installApplication( app, config.getDatabaseUrl() );
            }
        }
        catch ( CouchDBException e )
        {
            throw new UserDataException(
                                         "Failed to install user-application: %s\nDatabase: %s.\nReason: %s",
                                         e, config.getLogicApplication(), config.getDatabaseUrl(),
                                         e.getMessage() );
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

}
