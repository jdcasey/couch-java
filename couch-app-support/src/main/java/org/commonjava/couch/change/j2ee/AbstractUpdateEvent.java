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
