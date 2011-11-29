/*******************************************************************************
 * Copyright 2011 John Casey
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
