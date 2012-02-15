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

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.commonjava.web.json.ser.WebSerializationAdapter;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

public class CouchDocChangeDeserializer
    implements JsonDeserializer<CouchDocChange>, WebSerializationAdapter
{

    private static final String SEQ = "seq";

    private static final String ID = "id";

    private static final String CHANGES_ARRAY = "changes";

    private static final String REV = "rev";

    private static final String DELETED = "deleted";

    @Override
    public CouchDocChange deserialize( final JsonElement json, final Type typeOfT,
                                       final JsonDeserializationContext context )
        throws JsonParseException
    {
        final JsonObject record = json.getAsJsonObject();
        final int seq = record.get( SEQ )
                              .getAsInt();
        final String id = record.get( ID )
                                .getAsString();

        final JsonElement element = record.get( DELETED );
        final boolean deleted = element == null ? false : element.getAsBoolean();

        final JsonArray changesArray = record.getAsJsonArray( CHANGES_ARRAY );
        final List<String> revs = new ArrayList<String>( changesArray.size() );
        for ( final JsonElement revRecord : changesArray )
        {
            revs.add( revRecord.getAsJsonObject()
                               .get( REV )
                               .getAsString() );
        }

        return new CouchDocChange( seq, id, revs, deleted );
    }

    @Override
    public void register( final GsonBuilder gsonBuilder )
    {
        gsonBuilder.registerTypeAdapter( CouchDocChange.class, this );
    }

}
