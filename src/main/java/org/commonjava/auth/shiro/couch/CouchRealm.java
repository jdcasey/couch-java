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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAccount;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.Permission;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.subject.PrincipalCollection;
import org.commonjava.auth.couch.data.UserDataException;
import org.commonjava.auth.couch.data.UserDataManager;
import org.commonjava.auth.couch.model.Role;
import org.commonjava.auth.couch.model.User;
import org.commonjava.auth.shiro.couch.model.ShiroPermission;
import org.commonjava.auth.shiro.couch.model.ShiroUser;
import org.commonjava.util.logging.Logger;

public class CouchRealm
    extends AuthorizingRealm
{

    private final Logger logger = new Logger( getClass() );

    @Inject
    private UserDataManager dataManager;

    @Inject
    private CouchPermissionResolver resolver;

    CouchRealm()
    {}

    public CouchRealm( final UserDataManager dataManager, final CouchPermissionResolver resolver )
    {
        this.dataManager = dataManager;
        this.resolver = resolver;

        initResolver();
    }

    public void setupSecurityManager( final Realm... fallbackRealms )
    {
        List<Realm> realms = new ArrayList<Realm>();
        realms.add( this );
        for ( Realm realm : fallbackRealms )
        {
            if ( realm != null )
            {
                realms.add( realm );
            }
        }

        SecurityUtils.setSecurityManager( new DefaultSecurityManager( realms ) );
    }

    @PostConstruct
    protected void initResolver()
    {
        setRolePermissionResolver( resolver );
        setPermissionResolver( resolver );
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo( final PrincipalCollection principals )
    {
        final Object principal = principals.getPrimaryPrincipal();
        User user;
        try
        {
            user = dataManager.getUser( principal.toString() );
        }
        catch ( UserDataException e )
        {
            logger.error( "Failed to retrieve user: %s. Reason: %s", e, principal, e.getMessage() );

            throw new AuthenticationException(
                                               "Cannot retrieve user. System configuration is invalid." );
        }

        if ( user == null )
        {
            throw new AuthenticationException( "Authentication failed: " + principal );
        }

        final Set<String> roleNames = new HashSet<String>();
        final Set<Permission> perms = new HashSet<Permission>();
        if ( user.getRoles() != null )
        {
            Set<Role> roles;
            try
            {
                roles = dataManager.getRoles( user );
            }
            catch ( UserDataException e )
            {
                logger.error( "Failed to retrieve roles for user: %s. Reason: %s", e, principal,
                              e.getMessage() );

                throw new AuthenticationException(
                                                   "Cannot retrieve user roles. System configuration is invalid." );
            }

            for ( final Role role : roles )
            {
                roleNames.add( role.getName() );

                Set<org.commonjava.auth.couch.model.Permission> permissions;
                try
                {
                    permissions = dataManager.getPermissions( role );
                }
                catch ( UserDataException e )
                {
                    logger.error( "Failed to retrieve permissions for role: %s. Reason: %s", e,
                                  role.getName(), e.getMessage() );

                    throw new AuthenticationException(
                                                       "Cannot retrieve role permissions. System configuration is invalid." );
                }

                if ( permissions != null )
                {
                    for ( org.commonjava.auth.couch.model.Permission perm : permissions )
                    {
                        perms.add( new ShiroPermission( perm ) );
                    }
                }
            }
        }

        return new SimpleAccount( principals, user.getPasswordDigest(), roleNames, perms );
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo( final AuthenticationToken token )
        throws AuthenticationException
    {
        if ( !( token instanceof UsernamePasswordToken ) )
        {
            throw new AuthenticationException( "Cannot use authentication token of type: "
                + token.getClass().getName() + " with this service." );
        }

        final UsernamePasswordToken tok = (UsernamePasswordToken) token;
        User user;
        try
        {
            user = dataManager.getUser( tok.getUsername() );
        }
        catch ( UserDataException e )
        {
            logger.error( "Failed to retrieve user: %s. Reason: %s", e, tok.getUsername(),
                          e.getMessage() );

            throw new AuthenticationException(
                                               "Cannot retrieve user. System configuration is invalid." );
        }

        return ShiroUser.getAuthenticationInfo( user );
    }

}
