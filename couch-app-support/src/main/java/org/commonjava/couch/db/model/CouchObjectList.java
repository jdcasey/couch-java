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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;

public class CouchObjectList<T>
    implements Iterable<T>
{

    private ArrayList<T> items;

    CouchObjectList()
    {
        items = new ArrayList<T>();
    }

    public CouchObjectList( final List<T> items )
    {
        setItems( items );
    }

    public List<T> getItems()
    {
        return items;
    }

    void setItems( final List<T> items )
    {
        if ( items != null )
        {
            this.items = new ArrayList<T>( new LinkedHashSet<T>( items ) );
        }
        else
        {
            this.items = new ArrayList<T>();
        }
    }

    @Override
    public Iterator<T> iterator()
    {
        return items.iterator();
    }

}
