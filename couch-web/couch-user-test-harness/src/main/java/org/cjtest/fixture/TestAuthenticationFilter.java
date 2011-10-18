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
package org.cjtest.fixture;

import java.io.IOException;
import java.util.Properties;

import javax.inject.Inject;
import javax.inject.Named;
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
import org.commonjava.auth.couch.model.User;
import org.commonjava.auth.shiro.couch.model.ShiroUserUtils;
import org.commonjava.couch.test.fixture.TestPropertyDefinitions;

@WebFilter( "/*" )
public class TestAuthenticationFilter
    implements Filter
{
    @Inject
    private UserDataManager dataManager;

    @Inject
    @Named( TestPropertyDefinitions.NAMED )
    private Properties testProperties;

    @Override
    public void init( final FilterConfig filterConfig )
        throws ServletException
    {}

    @Override
    public void doFilter( final ServletRequest request, final ServletResponse response,
                          final FilterChain chain )
        throws IOException, ServletException
    {
        // Login the user before we test!
        Subject subject = SecurityUtils.getSubject();

        String username =
            testProperties.getProperty( TestPropertyDefinitions.AUTHENTICATE_USER, User.ADMIN );

        User user;
        try
        {
            user = dataManager.getUser( username );
        }
        catch ( UserDataException e )
        {
            throw new ServletException( "Cannot find user: " + username
                + " to authenticate! Error: " + e.getMessage(), e );
        }

        subject.login( ShiroUserUtils.getAuthenticationToken( user ) );

        chain.doFilter( request, response );
    }

    @Override
    public void destroy()
    {}

}
