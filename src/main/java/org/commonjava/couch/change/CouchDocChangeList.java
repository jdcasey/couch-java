package org.commonjava.couch.change;

import java.util.Iterator;
import java.util.List;

import com.google.gson.annotations.SerializedName;

public class CouchDocChangeList
    implements Iterable<CouchDocChange>
{

    private List<CouchDocChange> results;

    @SerializedName( "last_seq" )
    private int lastSequence;

    CouchDocChangeList()
    {}

    public List<CouchDocChange> getResults()
    {
        return results;
    }

    void setResults( final List<CouchDocChange> results )
    {
        this.results = results;
    }

    public int getLastSequence()
    {
        return lastSequence;
    }

    void setLastSequence( final int lastSequence )
    {
        this.lastSequence = lastSequence;
    }

    @Override
    public Iterator<CouchDocChange> iterator()
    {
        return results.iterator();
    }

}
