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

import static org.apache.commons.lang.StringUtils.join;
import static org.commonjava.auth.couch.util.IdUtils.namespaceId;

import org.commonjava.couch.model.AbstractCouchDocument;

import com.google.gson.annotations.Expose;

public class Permission
    extends AbstractCouchDocument
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
    private final String docType = NAMESPACE;

    public Permission()
    {}

    public Permission( final String firstPart, final String... nameParts )
    {
        this.name = name( firstPart, nameParts );
        setCouchDocId( namespaceId( NAMESPACE, this.name ) );
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
        final Permission other = (Permission) obj;
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
        return firstPart
            + ( ( parts != null && parts.length > 0 ) ? ( ":" + join( parts, ":" ) ) : "" );
    }

    public String getDocType()
    {
        return docType;
    }

}
