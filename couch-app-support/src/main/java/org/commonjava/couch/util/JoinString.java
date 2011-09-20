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
package org.commonjava.couch.util;

import static org.apache.commons.lang.StringUtils.join;

import java.util.Arrays;
import java.util.Collection;

public class JoinString
{

    private final Collection<?> items;

    private final String joint;

    public JoinString( final String joint, final Collection<?> items )
    {
        this.items = items;
        this.joint = joint;
    }

    public JoinString( final String joint, final Object... items )
    {
        this.items = Arrays.asList( items );
        this.joint = joint;
    }

    @Override
    public String toString()
    {
        return items == null || items.isEmpty() ? "-NONE-" : join( items, joint );
    }

}
