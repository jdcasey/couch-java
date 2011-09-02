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
package org.commonjava.web.common.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Listing<T>
    implements Iterable<T>
{

    private List<T> items;

    public Listing()
    {
    }

    public Listing( final T... elements )
    {
        items = Arrays.asList( elements );
    }

    public Listing( final Collection<T> elements )
    {
        this.items = new ArrayList<T>( elements );
    }

    public List<T> getItems()
    {
        return items;
    }

    public void setItems( final List<T> items )
    {
        this.items = items;
    }

    @Override
    public Iterator<T> iterator()
    {
        return items.iterator();
    }

}
