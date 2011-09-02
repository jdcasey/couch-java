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
package org.commonjava.auth.shiro.couch.model;

import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.commonjava.auth.couch.model.User;

public class ShiroUser
    extends User
{

    private static final String DEFAULT_REALM = "default";

    public static AuthenticationInfo getAuthenticationInfo( final User user )
    {
        // TODO: make the user able to store properties, so we can set different realms...?
        String realm = DEFAULT_REALM;
        return new SimpleAuthenticationInfo( user.getUsername(), user.getPasswordDigest(), realm );
    }

    public static AuthenticationToken getAuthenticationToken( final User user )
    {
        return new UsernamePasswordToken( user.getUsername(), user.getPasswordDigest() );
    }

}
