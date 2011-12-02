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
package org.commonjava.couch.rbac;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class Role
    extends ModelMetadata
{

    public static final String ADMIN = "admin";

    public static final String NAMESPACE = "role";

    private String name;

    private Set<String> permissions;

    Role()
    {
    }

    public Role( final String name, final Permission... perms )
    {
        setName( name );
        this.permissions = new HashSet<String>( perms.length );
        for ( final Permission perm : perms )
        {
            this.permissions.add( perm.getName() );
        }
    }

    public Role( final String name, final Collection<Permission> perms )
    {
        this.name = name;
        this.permissions = new HashSet<String>( perms.size() );
        for ( final Permission perm : perms )
        {
            this.permissions.add( perm.getName() );
        }
    }

    public Role( final String name, final Set<String> permissions )
    {
        this.name = name;
        this.permissions = permissions;
    }

    public String getName()
    {
        return name;
    }

    void setName( final String name )
    {
        this.name = name;
    }

    public synchronized boolean addPermission( final Permission permission )
    {
        if ( permission == null )
        {
            return false;
        }

        return addPermission( permission.getName() );
    }

    public synchronized boolean addPermission( final String name )
    {
        if ( permissions == null )
        {
            permissions = new HashSet<String>();
        }

        return permissions.add( name );
    }

    public boolean removePermission( final Permission permission )
    {
        if ( permission == null )
        {
            return false;
        }

        return removePermission( permission.getName() );
    }

    public boolean removePermission( final String name )
    {
        if ( permissions != null )
        {
            return permissions.remove( name );
        }

        return false;
    }

    public Set<String> getPermissions()
    {
        return permissions;
    }

    public void setPermissions( final Set<Permission> permissions )
    {
        if ( this.permissions == null )
        {
            this.permissions = new HashSet<String>();
        }
        else
        {
            this.permissions.clear();
        }
        if ( permissions != null )
        {
            for ( final Permission permission : permissions )
            {
                this.permissions.add( permission.getName() );
            }
        }
    }

    public void setPermissionNames( final Set<String> permissions )
    {
        if ( this.permissions == null )
        {
            this.permissions = new HashSet<String>();
        }
        else
        {
            this.permissions.clear();
        }
        if ( permissions != null )
        {
            for ( final String permission : permissions )
            {
                this.permissions.add( permission );
            }
        }
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + name.hashCode();
        return result;
    }

    @Override
    public boolean equals( final Object obj )
    {
        if ( this == obj )
        {
            return true;
        }
        if ( obj == null )
        {
            return false;
        }
        if ( getClass() != obj.getClass() )
        {
            return false;
        }
        final Role other = (Role) obj;
        if ( !name.equals( other.name ) )
        {
            return false;
        }
        return true;
    }

    public boolean containsPermission( final Permission perm )
    {
        return permissions != null && permissions.contains( perm );
    }

    @Override
    public String toString()
    {
        final StringBuilder builder = new StringBuilder();
        builder.append( "Role [name=" )
               .append( name )
               .append( "\n\tpermissions=" )
               .append( permissions )
               .append( "]" );
        return builder.toString();
    }

}
