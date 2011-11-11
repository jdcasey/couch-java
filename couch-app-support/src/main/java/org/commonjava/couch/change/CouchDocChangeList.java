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

import java.util.Collections;
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
    {
    }

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
        return results == null ? Collections.<CouchDocChange> emptySet()
                                            .iterator() : results.iterator();
    }

}
