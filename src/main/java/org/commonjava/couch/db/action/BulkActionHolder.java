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
