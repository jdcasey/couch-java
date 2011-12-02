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
package org.commonjava.auth.shiro.couch.model;

import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.commonjava.couch.rbac.User;

public class ShiroUserUtils
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
