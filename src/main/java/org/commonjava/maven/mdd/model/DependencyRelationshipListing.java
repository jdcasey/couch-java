package org.commonjava.maven.mdd.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;

public class DependencyRelationshipListing
    implements Iterable<DependencyRelationship>
{

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
            dependencies =
                new ArrayList<DependencyRelationship>(
                                                       new LinkedHashSet<DependencyRelationship>(
                                                                                                  deps ) );
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

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append( "Dependency-Relationship Listing: [\n" );
        if ( dependencies != null && !dependencies.isEmpty() )
        {
            for ( DependencyRelationship rel : dependencies )
            {
                sb.append( "  " ).append( rel ).append( "\n" );
            }
        }
        sb.append( "]" );

        return sb.toString();
    }

}
