package org.commonjava.couch.change;

import java.util.List;

public class CouchDocChange
{

    private final int sequence;

    private final String id;

    private final List<String> revisions;

    private final boolean deleted;

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

    @Override
    public String toString()
    {
        return String.format( "CouchDocChange [sequence=%s, id=%s, revisions=%s, deleted=%s]",
                              sequence, id, revisions, deleted );
    }

}
