package org.commonjava.couch.change;

import java.util.List;

public class CouchDocChange
{

    private int sequence;

    private String id;

    private List<String> revisions;

    private boolean deleted;

    CouchDocChange()
    {}

    public CouchDocChange( final int sequence, final String id, final List<String> revisions,
                           final boolean deleted )
    {
        this.sequence = sequence;
        this.id = id;
        this.revisions = revisions;
        this.deleted = deleted;
    }

    public int getSequence()
    {
        return sequence;
    }

    public String getId()
    {
        return id;
    }

    public List<String> getRevisions()
    {
        return revisions;
    }

    public boolean isDeleted()
    {
        return deleted;
    }

    protected void setSequence( final int sequence )
    {
        this.sequence = sequence;
    }

    protected void setId( final String id )
    {
        this.id = id;
    }

    protected void setRevisions( final List<String> revisions )
    {
        this.revisions = revisions;
    }

    protected void setDeleted( final boolean deleted )
    {
        this.deleted = deleted;
    }

}
