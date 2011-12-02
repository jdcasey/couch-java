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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAccount;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.Permission;
import org.apache.shiro.cache.MemoryConstrainedCacheManager;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.subject.PrincipalCollection;
import org.commonjava.auth.couch.data.UserDataException;
import org.commonjava.auth.couch.data.UserDataManager;
import org.commonjava.auth.shiro.couch.model.ShiroPermission;
import org.commonjava.auth.shiro.couch.model.ShiroUserUtils;
import org.commonjava.couch.rbac.Role;
import org.commonjava.couch.rbac.User;
import org.commonjava.util.logging.Logger;

@Singleton
public class CouchRealm
    extends AuthorizingRealm
{

    private final Logger logger = new Logger( getClass() );

    @Inject
    private UserDataManager dataManager;

    @Inject
    private CouchPermissionResolver resolver;

    private SecurityManager sm;

    CouchRealm()
    {
    }

    public CouchRealm( final UserDataManager dataManager, final CouchPermissionResolver resolver )
    {
        this.dataManager = dataManager;
        this.resolver = resolver;

        initRealm();
    }

    public void setupSecurityManager( final Realm... fallbackRealms )
    {
        // make indempotent.
        if ( sm == null )
        {
            final List<Realm> realms = new ArrayList<Realm>();
            realms.add( this );
            for ( final Realm realm : fallbackRealms )
            {
                if ( realm != null )
                {
                    realms.add( realm );
                }
            }

            sm = new DefaultSecurityManager( realms );
            SecurityUtils.setSecurityManager( sm );
        }
    }

    @PostConstruct
    protected void initRealm()
    {
        setRolePermissionResolver( resolver );
        setPermissionResolver( resolver );
        setCacheManager( new MemoryConstrainedCacheManager() );
        setCachingEnabled( true );
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
        catch ( final UserDataException e )
        {
            logger.error( "Failed to retrieve user: %s. Reason: %s", e, principal, e.getMessage() );

            throw new AuthenticationException( "Cannot retrieve user. System configuration is invalid." );
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
            catch ( final UserDataException e )
            {
                logger.error( "Failed to retrieve roles for user: %s. Reason: %s", e, principal, e.getMessage() );

                throw new AuthenticationException( "Cannot retrieve user roles. System configuration is invalid." );
            }

            for ( final Role role : roles )
            {
                roleNames.add( role.getName() );

                Set<org.commonjava.couch.rbac.Permission> permissions;
                try
                {
                    permissions = dataManager.getPermissions( role );
                }
                catch ( final UserDataException e )
                {
                    logger.error( "Failed to retrieve permissions for role: %s. Reason: %s", e, role.getName(),
                                  e.getMessage() );

                    throw new AuthenticationException(
                                                       "Cannot retrieve role permissions. System configuration is invalid." );
                }

                if ( permissions != null )
                {
                    for ( final org.commonjava.couch.rbac.Permission perm : permissions )
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
            throw new AuthenticationException( "Cannot use authentication token of type: " + token.getClass()
                                                                                                  .getName()
                + " with this service." );
        }

        final UsernamePasswordToken tok = (UsernamePasswordToken) token;
        User user;
        try
        {
            user = dataManager.getUser( tok.getUsername() );
        }
        catch ( final UserDataException e )
        {
            logger.error( "Failed to retrieve user: %s. Reason: %s", e, tok.getUsername(), e.getMessage() );

            throw new AuthenticationException( "Cannot retrieve user. System configuration is invalid." );
        }

        return ShiroUserUtils.getAuthenticationInfo( user );
    }

    public void setAutoCreateAuthorizationInfo( final boolean autoCreate )
    {
        resolver.setAutoCreateAuthorizationInfo( autoCreate );
    }

    public boolean isAutoCreateAuthorizationInfo()
    {
        return resolver.isAutoCreateAuthorizationInfo();
    }

}
