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
package org.cjtest.fixture;

import java.io.IOException;

import javax.inject.Inject;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.commonjava.auth.couch.data.UserDataException;
import org.commonjava.auth.couch.data.UserDataManager;
import org.commonjava.auth.shiro.couch.CouchRealm;
import org.commonjava.auth.shiro.couch.model.ShiroUserUtils;
import org.commonjava.couch.rbac.User;
import org.commonjava.util.logging.Logger;

@WebFilter( "/*" )
public class TestAuthenticationFilter
    implements Filter
{

    private final Logger logger = new Logger( getClass() );

    @Inject
    private TestAuthenticationControls controls;

    @Inject
    private CouchRealm realm;

    @Inject
    private UserDataManager dataManager;

    @Override
    public void init( final FilterConfig filterConfig )
        throws ServletException
    {
        logger.info( "\n\nStarting up TEST LOGIN filter...\nControls:\n\t%s\nRealm:\n\t%s\n\n", controls, realm );
        if ( controls.isDoAuthentication() )
        {
            setupRealm();
        }
    }

    @Override
    public void doFilter( final ServletRequest request, final ServletResponse response, final FilterChain chain )
        throws IOException, ServletException
    {
        logger.info( "TEST LOGIN FILTER..." );
        if ( controls.isDoAuthentication() )
        {
            logger.info( "TEST LOGIN: CouchDB Shiro" );

            final Subject subject = SecurityUtils.getSubject();

            // if there is already a user logged in, log them out.
            if ( subject.isAuthenticated() )
            {
                subject.logout();
            }

            final String username = controls.getUser();

            logger.info( "TEST LOGIN: User is '" + username + "'" );

            User user;
            try
            {
                user = dataManager.getUser( username );
            }
            catch ( final UserDataException e )
            {
                throw new ServletException( "Cannot find user: " + username + " to authenticate! Error: "
                    + e.getMessage(), e );
            }

            if ( user == null )
            {
                throw new ServletException( "Cannot find user: " + username + " to authenticate!" );
            }

            // Login the user before we test!
            subject.login( ShiroUserUtils.getAuthenticationToken( user ) );

            logger.info( "/TEST LOGIN: CouchDB Shiro" );
        }
        logger.info( "/TEST LOGIN FILTER..." );
        chain.doFilter( request, response );

    }

    private void setupRealm()
    {
        logger.info( "Initializing CouchDB Shiro authentication/authorization realm..." );
        if ( realm == null )
        {
            throw new RuntimeException( "Failed to initialize security. Realm has not been injected!" );
        }

        realm.setupSecurityManager();
        realm.setAutoCreateAuthorizationInfo( controls.isAutoCreateAuthorizations() );

        logger.info( "...done." );
    }

    @Override
    public void destroy()
    {
    }

}
