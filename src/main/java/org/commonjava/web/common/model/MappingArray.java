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

import static org.apache.commons.lang.StringUtils.join;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class MappingArray
    implements Iterable<String>
{

    private String[] elements;

    public String[] getElements()
    {
        return elements;
    }

    public void setElements( final String[] elements )
    {
        this.elements = elements;
    }

    @Override
    public Iterator<String> iterator()
    {
        List<String> eltList;
        if ( elements == null )
        {
            eltList = Collections.emptyList();
        }
        else
        {
            eltList = Arrays.asList( elements );
        }

        return eltList.iterator();
    }

    @Override
    public String toString()
    {
        return elements == null ? "-NONE-" : join( elements, ", " );
    }

}
