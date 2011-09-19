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
package org.commonjava.couch.db.action;

import java.util.Collection;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BulkActionHolder
{

    @SuppressWarnings( "unused" )
    @SerializedName( "all_or_nothing" )
    private boolean transactional = false;

    @SuppressWarnings( "unused" )
    @SerializedName( "non_atomic" )
    private boolean nonAtomic = true;

    @SerializedName( "docs" )
    @Expose( deserialize = false )
    private final CouchDocumentAction[] actions;

    public BulkActionHolder( final Collection<? extends CouchDocumentAction> actions )
    {
        this.actions = actions.toArray( new CouchDocumentAction[] {} );
    }

    public BulkActionHolder( final Collection<? extends CouchDocumentAction> actions,
                             final boolean transactional )
    {
        this.actions = actions.toArray( new CouchDocumentAction[] {} );
        this.transactional = transactional;
        this.nonAtomic = !transactional;
    }

    public CouchDocumentAction[] getActions()
    {
        return actions;
    }

}
