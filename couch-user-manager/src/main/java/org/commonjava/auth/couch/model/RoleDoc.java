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
package org.commonjava.auth.couch.model;

import static org.commonjava.auth.couch.model.MetadataKeys.ID_METADATA;
import static org.commonjava.auth.couch.model.MetadataKeys.REV_METADATA;
import static org.commonjava.couch.util.IdUtils.namespaceId;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.commonjava.couch.model.AbstractCouchDocument;
import org.commonjava.couch.model.DenormalizedCouchDoc;
import org.commonjava.couch.rbac.Permission;
import org.commonjava.couch.rbac.Role;

import com.google.gson.annotations.Expose;

public class RoleDoc
    extends AbstractCouchDocument
    implements DenormalizedCouchDoc
{

    public static final String ADMIN = "admin";

    public static final String NAMESPACE = "role";

    private String name;

    private Set<String> permissions;

    @Expose( deserialize = false )
    private final String doctype = NAMESPACE;

    RoleDoc()
    {
    }

    public RoleDoc( final String name, final Permission... perms )
    {
        setName( name );
        this.permissions = new HashSet<String>( perms.length );
        for ( final Permission perm : perms )
        {
            this.permissions.add( perm.getName() );
        }

        calculateDenormalizedFields();
    }

    public RoleDoc( final Role role )
    {
        setName( role.getName() );
        this.permissions = new HashSet<String>( role.getPermissions() );
        setCouchDocRev( role.getMetadata( REV_METADATA, String.class ) );

        calculateDenormalizedFields();
    }

    public RoleDoc( final String name, final Collection<Permission> perms )
    {
        this.name = name;
        this.permissions = new HashSet<String>( perms.size() );
        for ( final Permission perm : perms )
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
        final RoleDoc other = (RoleDoc) obj;
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

    public String getDoctype()
    {
        return doctype;
    }

    @Override
    public void calculateDenormalizedFields()
    {
        setCouchDocId( namespaceId( NAMESPACE, this.name ) );
    }

    public static Set<Role> toRoleSet( final List<RoleDoc> docs )
    {
        final Set<Role> result = new HashSet<Role>();
        for ( final RoleDoc doc : docs )
        {
            result.add( doc.toRole() );
        }
        return result;
    }

    public Role toRole()
    {
        final Role r = new Role( name, permissions );
        r.setMetadata( REV_METADATA, getCouchDocRev() );
        r.setMetadata( ID_METADATA, getCouchDocId() );
        return r;
    }

    public static Collection<RoleDoc> toDocuments( final Collection<Role> roles )
    {
        final Set<RoleDoc> docs = new HashSet<RoleDoc>();
        for ( final Role role : roles )
        {
            docs.add( new RoleDoc( role ) );
        }
        return docs;
    }

}
