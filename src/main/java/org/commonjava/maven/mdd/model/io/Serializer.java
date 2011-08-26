package org.commonjava.maven.mdd.model.io;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;

import org.codehaus.plexus.component.annotations.Component;
import org.commonjava.maven.mdd.model.DatabaseError;
import org.commonjava.maven.mdd.model.DependencyRelationship;
import org.commonjava.maven.mdd.model.DependencyRelationshipListing;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

@Component( role = Serializer.class )
public class Serializer
{

    public String toString( final DependencyRelationship dep )
    {
        return getGson().toJson( dep );
    }

    public DependencyRelationship toDependency( final String src )
    {
        return getGson().fromJson( src, DependencyRelationship.class );
    }

    public DependencyRelationshipListing toDependencyListing( final String src )
    {
        return getGson().fromJson( src, DependencyRelationshipListing.class );
    }

    private Gson getGson()
    {
        GsonBuilder builder = new GsonBuilder();
        // builder.generateNonExecutableJson();
        builder.setPrettyPrinting();
        builder.registerTypeAdapter( DependencyRelationshipListing.class,
                                     new DependencyRelationshipListingAdapter() );

        return builder.create();
    }

    public DependencyRelationshipListing toDependencyListing( final InputStream in,
                                                              final String charset )
        throws UnsupportedEncodingException
    {
        Reader reader = new InputStreamReader( in, charset );
        return getGson().fromJson( reader, DependencyRelationshipListing.class );
    }

    public DependencyRelationship toDependency( final InputStream in, final String charset )
        throws UnsupportedEncodingException
    {
        Reader reader = new InputStreamReader( in, charset );
        return getGson().fromJson( reader, DependencyRelationship.class );
    }

    public DatabaseError toError( final InputStream in, final String charset )
        throws UnsupportedEncodingException
    {
        Reader reader = new InputStreamReader( in, charset );
        return getGson().fromJson( reader, DatabaseError.class );
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
