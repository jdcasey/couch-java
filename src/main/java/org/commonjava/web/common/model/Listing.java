package org.commonjava.web.common.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class Listing<T>
    implements Iterable<T>
{

    private List<T> items;

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
