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
package org.commonjava.auth.couch.model;

import static org.commonjava.auth.couch.util.IdUtils.namespaceId;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.commonjava.couch.model.AbstractCouchDocument;
import org.commonjava.couch.model.DenormalizedCouchDoc;

import com.google.gson.annotations.Expose;

public class Role
    extends AbstractCouchDocument
    implements DenormalizedCouchDoc
{

    public static final String ADMIN = "admin";

    public static final String NAMESPACE = "role";

    private String name;

    private Set<String> permissions;

    @Expose( deserialize = false )
    private final String doctype = NAMESPACE;

    Role()
    {}

    public Role( final String name, final Permission... perms )
    {
        setName( name );
        this.permissions = new HashSet<String>( perms.length );
        for ( Permission perm : perms )
        {
            this.permissions.add( perm.getName() );
        }

        calculateDenormalizedFields();
    }

    public Role( final String name, final Collection<Permission> perms )
    {
        this.name = name;
        this.permissions = new HashSet<String>( perms.size() );
        for ( Permission perm : perms )
        {
            this.permissions.add( perm.getName() );
        }

        calculateDenormalizedFields();
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
            for ( Permission permission : permissions )
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
            for ( String permission : permissions )
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
        StringBuilder builder = new StringBuilder();
        builder.append( "Role [name=" ).append( name ).append( "\n\tpermissions=" ).append( permissions ).append( "]" );
        return builder.toString();
    }

    public String getDoctype()
    {
        return doctype;
    }

    @Override
    public void calculateDenormalizedFields()
    {
        setCouchDocId( namespaceId( NAMESPACE, this.name ) );
    }

}
