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
