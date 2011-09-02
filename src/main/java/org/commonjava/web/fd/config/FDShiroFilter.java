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
package org.commonjava.web.fd.config;

import javax.inject.Inject;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;

import org.commonjava.auth.shiro.couch.CouchRealm;

@WebFilter( filterName = "shiro", urlPatterns = "/*" )
public class FDShiroFilter
{

    @Inject
    private CouchRealm realm;

    public void init( final FilterConfig filterConfig )
        throws ServletException
    {
        if ( realm == null )
        {
            throw new ServletException(
                                        "Failed to initialize security. Realm has not been injected!" );
        }

        realm.setupSecurityManager();
    }

}
