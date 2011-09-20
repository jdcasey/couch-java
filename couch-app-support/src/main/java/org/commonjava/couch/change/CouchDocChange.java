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
