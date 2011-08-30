package org.commonjava.couch.model.io;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.commonjava.couch.db.model.CouchObjectList;
import org.commonjava.couch.model.CouchDocument;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

public class CouchObjectListDeserializer<T extends CouchDocument>
    implements JsonDeserializer<CouchObjectList<T>>, SerializationAdapter
{

    private static final String ROWS = "rows";

    private static final String DOC_ELEMENT = "doc";

    private final Class<T> type;

    public CouchObjectListDeserializer( final Class<T> type )
    {
        this.type = type;
    }

    @Override
    public Type typeLiteral()
    {
        return new TypeToken<CouchObjectList<T>>()
        {}.getType();
    }

    @Override
    public CouchObjectList<T> deserialize( final JsonElement json, final Type typeOfT,
                                           final JsonDeserializationContext context )
        throws JsonParseException
    {
        List<T> items = new ArrayList<T>();

        JsonElement rowsRaw = json.getAsJsonObject().get( ROWS );
        if ( rowsRaw == null )
        {
            throw new JsonParseException( "Cannot find " + ROWS + " field within root object." );
        }

        JsonArray rows = rowsRaw.getAsJsonArray();
        for ( JsonElement row : rows )
        {
            JsonObject rowObj = row.getAsJsonObject();
            JsonElement doc = rowObj.get( DOC_ELEMENT );
            if ( doc == null )
            {
                throw new JsonParseException( "Cannot find " + DOC_ELEMENT + " field within row: "
                    + row
                    + "\nDid you access the view with the '?include_docs=true' query parameter?" );
            }

            items.add( type.cast( context.deserialize( doc, type ) ) );
        }

        return new CouchObjectList<T>( items );
    }

}
