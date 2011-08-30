/*******************************************************************************
 * Copyright (C) 2011  John Casey
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package org.commonjava.maven.mdd.model.io;

import java.lang.reflect.Type;

import org.codehaus.plexus.component.annotations.Component;
import org.commonjava.maven.mdd.model.DependencyRelationship;
import org.commonjava.maven.mdd.model.DependencyRelationshipListing;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

@Component( role = MDDSerializer.class )
public class MDDSerializer
    extends org.commonjava.couch.model.io.Serializer
{

    @Override
    protected GsonBuilder newGsonBuilder()
    {
        GsonBuilder builder = super.newGsonBuilder();
        builder.registerTypeAdapter( DependencyRelationshipListing.class,
                                     new DependencyRelationshipListingAdapter() );

        return builder;
    }

    private static final class DependencyRelationshipListingAdapter
        implements JsonDeserializer<DependencyRelationshipListing>
    {
        @Override
        public DependencyRelationshipListing deserialize( final JsonElement json,
                                                          final Type typeOfT,
                                                          final JsonDeserializationContext context )
            throws JsonParseException
        {
            JsonArray arry = json.getAsJsonObject().get( "rows" ).getAsJsonArray();
            DependencyRelationship[] rels = new DependencyRelationship[arry.size()];

            for ( int i = 0; i < rels.length; i++ )
            {
                JsonElement val = arry.get( i ).getAsJsonObject().get( "doc" );
                rels[i] = context.deserialize( val, DependencyRelationship.class );
            }

            return new DependencyRelationshipListing( rels );
        }
    }

}
