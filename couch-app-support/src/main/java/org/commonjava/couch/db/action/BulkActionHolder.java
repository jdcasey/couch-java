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
