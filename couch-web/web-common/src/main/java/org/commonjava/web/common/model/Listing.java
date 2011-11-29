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
