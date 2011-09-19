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
package org.commonjava.couch.io.json;

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
