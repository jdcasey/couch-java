package org.commonjava.couch.change.dispatch;

import org.commonjava.couch.change.CouchDocChange;

public final class CouchChangeJ2EEEvent
{
    private final CouchDocChange change;

    CouchChangeJ2EEEvent( final CouchDocChange change )
    {
        this.change = change;
    }

    public CouchDocChange getChange()
    {
        return change;
    }

    @Override
    public String toString()
    {
        return String.format( "CouchChangeEvent [change=%s]", change );
    }
}