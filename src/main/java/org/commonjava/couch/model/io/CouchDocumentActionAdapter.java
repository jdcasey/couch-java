package org.commonjava.couch.model.io;

import java.lang.reflect.Type;

import org.commonjava.couch.db.action.CouchDocumentAction;

import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class CouchDocumentActionAdapter
    implements JsonSerializer<CouchDocumentAction>
{

    @Override
    public JsonElement serialize( final CouchDocumentAction src, final Type typeOfSrc,
                                  final JsonSerializationContext context )
    {
        return context.serialize( src.getDocument() );
    }

}
