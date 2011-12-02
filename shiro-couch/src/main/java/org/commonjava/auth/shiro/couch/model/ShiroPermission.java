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

import org.commonjava.couch.rbac.Permission;

public class ShiroPermission
    implements org.apache.shiro.authz.Permission
{

    // private final Logger logger = new Logger( getClass() );

    private final Permission permission;

    public ShiroPermission( final Permission permission )
    {
        this.permission = permission;
    }

    @Override
    public boolean implies( final org.apache.shiro.authz.Permission p )
    {
        // logger.info( "Checking whether permission: '%s' implies permission: '%s'",
        // this.permission.getName(), ( (ShiroPermission) p ).permission.getName() );

        String name = permission.getName();
        if ( name.equals( Permission.WILDCARD ) )
        {
            // logger.info( "YES(1)" );
            return true;
        }

        if ( name.endsWith( Permission.WILDCARD ) && ( p instanceof ShiroPermission ) )
        {
            ShiroPermission perm = (ShiroPermission) p;
            String prefix = name.substring( 0, name.length() - Permission.WILDCARD.length() );

            String permName = perm.permission.getName();
            boolean result = permName.length() > prefix.length() && permName.startsWith( prefix );
            // logger.info( result ? "YES(2)" : "NO(2)" );

            return result;
        }

        // logger.info( "NO(3)" );
        return false;
    }

}
