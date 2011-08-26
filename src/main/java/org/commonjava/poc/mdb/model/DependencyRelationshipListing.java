package org.commonjava.poc.mdb.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;

import com.google.gson.annotations.SerializedName;

public class DependencyRelationshipListing
    implements Iterable<DependencyRelationship>
{

    @SerializedName( "rows" )
    private List<DependencyRelationship> dependencies;

    DependencyRelationshipListing()
    {}

    public DependencyRelationshipListing( final DependencyRelationship... deps )
    {
        dependencies = Arrays.asList( deps );
    }

    public DependencyRelationshipListing( final Collection<DependencyRelationship> deps )
    {
        if ( deps == null )
        {
            dependencies = Collections.emptyList();
        }
        else
        {
            dependencies = new ArrayList<DependencyRelationship>( new LinkedHashSet<DependencyRelationship>( deps ) );
        }
    }

    public List<DependencyRelationship> getDependencies()
    {
        return dependencies;
    }

    void setDependencies( final List<DependencyRelationship> dependencies )
    {
        this.dependencies = dependencies;
    }

    @Override
    public Iterator<DependencyRelationship> iterator()
    {
        return dependencies.iterator();
    }

    public int size()
    {
        return dependencies.size();
    }

}
