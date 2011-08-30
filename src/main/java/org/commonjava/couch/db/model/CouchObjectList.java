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
