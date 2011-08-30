/*******************************************************************************
 * Copyright (C) 2011  John Casey
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
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
