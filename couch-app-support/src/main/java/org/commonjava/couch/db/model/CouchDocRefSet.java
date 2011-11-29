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
            i++;
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
