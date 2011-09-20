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
