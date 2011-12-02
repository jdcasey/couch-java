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

import static org.apache.commons.lang.StringUtils.join;
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

import com.google.gson.annotations.Expose;

public class PermissionDoc
    extends AbstractCouchDocument
    implements DenormalizedCouchDoc
{

    public static final String WILDCARD = "*";

    public static final String CREATE = "create";

    public static final String READ = "read";

    public static final String UPDATE = "update";

    public static final String DELETE = "delete";

    public static final String ADMIN = "admin";

    public static final String NAMESPACE = "permission";

    private String name;

    @Expose( deserialize = false )
    private final String doctype = NAMESPACE;

    PermissionDoc()
    {
    }

    public PermissionDoc( final String firstPart, final String... nameParts )
    {
        setName( name( firstPart, nameParts ) );
        calculateDenormalizedFields();
    }

    public PermissionDoc( final Permission permission )
    {
        setName( permission.getName() );
        setCouchDocRev( permission.getMetadata( REV_METADATA, String.class ) );
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
        final PermissionDoc other = (PermissionDoc) obj;
        if ( !name.equals( other.name ) )
        {
            return false;
        }
        return true;
    }

    @Override
    public String toString()
    {
        return String.format( "Permission@%d [%s]", hashCode(), name );
    }

    public static String name( final String firstPart, final String... parts )
    {
        return firstPart + ( ( parts != null && parts.length > 0 ) ? ( ":" + join( parts, ":" ) ) : "" );
    }

    public String getDoctype()
    {
        return doctype;
    }

    @Override
    public void calculateDenormalizedFields()
    {
        setCouchDocId( namespaceId( NAMESPACE, name ) );
    }

    public static Set<Permission> toPermissionSet( final List<PermissionDoc> docs )
    {
        final Set<Permission> perms = new HashSet<Permission>();
        for ( final PermissionDoc doc : docs )
        {
            perms.add( doc.toPermission() );
        }

        return perms;
    }

    public Permission toPermission()
    {
        final Permission p = new Permission( name );
        p.setMetadata( REV_METADATA, getCouchDocRev() );
        p.setMetadata( ID_METADATA, getCouchDocId() );
        return p;
    }

    public static Collection<PermissionDoc> toDocuments( final Collection<Permission> perms )
    {
        final Set<PermissionDoc> docs = new HashSet<PermissionDoc>();
        for ( final Permission perm : perms )
        {
            docs.add( new PermissionDoc( perm ) );
        }
        return docs;
    }

}
