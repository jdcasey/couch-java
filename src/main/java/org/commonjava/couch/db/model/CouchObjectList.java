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
