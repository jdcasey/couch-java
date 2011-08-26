package org.commonjava.poc.mdb.model.io;

import org.codehaus.plexus.component.annotations.Component;
import org.commonjava.poc.mdb.model.DependencyRelationship;
import org.commonjava.poc.mdb.model.DependencyRelationshipListing;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@Component( role = Serializer.class )
public class Serializer
{

    public String toString( final DependencyRelationship dep )
    {
        return getGson().toJson( dep );
    }

    public String toString( final DependencyRelationshipListing depList )
    {
        return getGson().toJson( depList );
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
        return new GsonBuilder()/* .generateNonExecutableJson() */.setPrettyPrinting().create();
    }

}
