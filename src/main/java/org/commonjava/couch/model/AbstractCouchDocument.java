/*******************************************************************************
 * Copyright (C) 2011  John Casey
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package org.commonjava.couch.model;

import com.google.gson.annotations.SerializedName;

public abstract class AbstractCouchDocument
    implements CouchDocument
{
    @SerializedName( "_id" )
    private String id;

    @SerializedName( "_rev" )
    private String revision;

    @Override
    public String getCouchDocId()
    {
        return id;
    }

    protected void setCouchDocId( final String id )
    {
        this.id = id;
    }

    @Override
    public String getCouchDocRev()
    {
        return revision;
    }

    @Override
    public void setCouchDocRev( final String revision )
    {
        this.revision = revision;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ( ( id == null ) ? 0 : id.hashCode() );
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
        AbstractCouchDocument other = (AbstractCouchDocument) obj;
        if ( id == null )
        {
            if ( other.id != null )
            {
                return false;
            }
        }
        else if ( !id.equals( other.id ) )
        {
            return false;
        }
        return true;
    }

}
