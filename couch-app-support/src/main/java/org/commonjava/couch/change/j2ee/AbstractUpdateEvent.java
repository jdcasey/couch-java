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
package org.commonjava.couch.change.j2ee;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;

public abstract class AbstractUpdateEvent<T>
    implements Iterable<T>
{

    private final Collection<T> changes;

    protected AbstractUpdateEvent( final Collection<T> changes )
    {
        this.changes =
            Collections.unmodifiableCollection( changes == null ? Collections.<T> emptySet()
                            : changes );
    }

    protected AbstractUpdateEvent( final T... changes )
    {
        this.changes = Collections.unmodifiableSet( new HashSet<T>( Arrays.asList( changes ) ) );
    }

    public final Collection<T> getChanges()
    {
        return changes;
    }

    @Override
    public final Iterator<T> iterator()
    {
        return changes.iterator();
    }

}
