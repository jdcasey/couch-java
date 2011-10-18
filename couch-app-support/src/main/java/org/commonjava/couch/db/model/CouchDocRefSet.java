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
package org.commonjava.couch.db.model;

import static org.apache.commons.lang.StringUtils.join;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.commonjava.couch.model.CouchDocRef;

import com.google.gson.annotations.Expose;

public class CouchDocRefSet
{

    @Expose( serialize = true, deserialize = false )
    private final String[] keys;

    public CouchDocRefSet( final CouchDocRef... keys )
    {
        this.keys = new String[keys.length];
        for ( int i = 0; i < keys.length; i++ )
        {
            this.keys[i] = keys[i].getCouchDocId();
        }
    }

    public CouchDocRefSet( final Collection<CouchDocRef> keys )
    {
        Set<CouchDocRef> refs = new HashSet<CouchDocRef>( keys );
        this.keys = new String[refs.size()];
        int i = 0;
        for ( CouchDocRef ref : refs )
        {
            this.keys[i] = ref.getCouchDocId();
        }
    }

    public String[] getKeys()
    {
        return keys;
    }

    @Override
    public String toString()
    {
        return "Couch document-reference set:\n\t" + join( keys, "\n\t" );
    }

}
