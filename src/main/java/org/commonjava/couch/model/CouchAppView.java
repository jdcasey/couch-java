package org.commonjava.couch.model;

public class CouchAppView
{
    private String map;

    private String reduce;

    CouchAppView()
    {}

    public CouchAppView( final String map, final String reduce )
    {
        this.map = map;
        this.reduce = reduce;
    }

    public CouchAppView( final String map )
    {
        this.map = map;
    }

    public String getMap()
    {
        return map;
    }

    void setMap( final String map )
    {
        this.map = map;
    }

    public String getReduce()
    {
        return reduce;
    }

    void setReduce( final String reduce )
    {
        this.reduce = reduce;
    }
}
