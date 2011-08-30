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

import org.apache.maven.mae.project.key.FullProjectKey;
import org.apache.maven.model.Dependency;
import org.commonjava.couch.model.CouchDocument;

import com.google.gson.annotations.SerializedName;

public class DependencyRelationship
    implements CouchDocument
{
    private static final String DEPENDENCY_RELATIONSHIP_DOCTYPE = "dependency-relationship";

    @SerializedName( "_id" )
    private String id;

    @SerializedName( "_rev" )
    private String rev;

    private Artifact dependency;

    private Artifact dependent;

    private String type;

    private String scope;

    @SerializedName( "doctype" )
    private String doctype = DEPENDENCY_RELATIONSHIP_DOCTYPE;

    private int index;

    DependencyRelationship()
    {}

    public DependencyRelationship( final Artifact dependency, final Artifact dependent,
                                   final String type, final String scope, final int index )
    {
        this.dependency = dependency;
        this.dependent = dependent;
        this.type = type;
        this.scope = scope == null ? "compile" : scope;
        this.index = index;
        generateId();
    }

    public DependencyRelationship( final Dependency dep, final FullProjectKey projectKey,
                                   final int index )
    {
        this.index = index;
        this.dependency = new Artifact( dep );
        this.dependent = new Artifact( projectKey );
        this.scope = dep.getScope() == null ? "compile" : dep.getScope();
        this.type = dep.getType();
        generateId();
    }

    public DependencyRelationship( final FullProjectKey dependent, final FullProjectKey dependency,
                                   final int index )
    {
        this.dependent = new Artifact( dependent );
        this.dependency = new Artifact( dependency );
        this.type = null;
        this.scope = null;
        this.index = index;
        generateId();
    }

    public DependencyRelationship( final Artifact dependency, final Artifact dependent,
                                   final int index )
    {
        this( dependency, dependent, "jar", null, index );
    }

    public DependencyRelationship( final FullProjectKey dependency, final FullProjectKey dependent )
    {
        this( dependency, dependent, -1 );
    }

    public DependencyRelationship( final Dependency dep, final FullProjectKey projectKey )
    {
        this( dep, projectKey, -1 );
    }

    private void generateId()
    {
        id = String.format( "%s_%s", dependent, dependency );
    }

    public Artifact getDependent()
    {
        return dependent;
    }

    void setDependent( final Artifact dependent )
    {
        this.dependent = dependent;
    }

    public Artifact getDependency()
    {
        return dependency;
    }

    void setDependency( final Artifact dependency )
    {
        this.dependency = dependency;
    }

    public String getType()
    {
        return type;
    }

    void setType( final String type )
    {
        this.type = type;
    }

    public String getScope()
    {
        return scope;
    }

    void setScope( final String scope )
    {
        this.scope = scope;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ( ( dependency == null ) ? 0 : dependency.hashCode() );
        result = prime * result + ( ( scope == null ) ? 0 : scope.hashCode() );
        result = prime * result + ( ( type == null ) ? 0 : type.hashCode() );
        result = prime * result + ( ( dependent == null ) ? 0 : dependent.hashCode() );
        return result;
    }

    @Override
    public boolean equals( final Object obj )
    {
        if ( this == obj )
        {
            return true;
        }
        if ( obj == null )
        {
            return false;
        }
        if ( getClass() != obj.getClass() )
        {
            return false;
        }
        DependencyRelationship other = (DependencyRelationship) obj;
        if ( dependency == null )
        {
            if ( other.dependency != null )
            {
                return false;
            }
        }
        else if ( !dependency.equals( other.dependency ) )
        {
            return false;
        }
        if ( scope == null )
        {
            if ( other.scope != null )
            {
                return false;
            }
        }
        else if ( !scope.equals( other.scope ) )
        {
            return false;
        }
        if ( type == null )
        {
            if ( other.type != null )
            {
                return false;
            }
        }
        else if ( !type.equals( other.type ) )
        {
            return false;
        }
        if ( dependent == null )
        {
            if ( other.dependent != null )
            {
                return false;
            }
        }
        else if ( !dependent.equals( other.dependent ) )
        {
            return false;
        }
        return true;
    }

    @Override
    public String toString()
    {
        return String.format( "Dependency [dependency=%s, dependent=%s, type=%s, scope=%s]",
                              dependency, dependent, type, scope );
    }

    String getDoctype()
    {
        return doctype;
    }

    void setDoctype( final String doctype )
    {
        this.doctype = doctype;
    }

    public String getId()
    {
        return id;
    }

    void setId( final String id )
    {
        this.id = id;
    }

    public String getRev()
    {
        return rev;
    }

    public void setRev( final String rev )
    {
        this.rev = rev;
    }

    @Override
    public String getCouchDocId()
    {
        return id;
    }

    @Override
    public String getCouchDocRev()
    {
        return rev;
    }

    @Override
    public void setCouchDocRev( final String revision )
    {
        this.rev = revision;
    }

    public int getIndex()
    {
        return index;
    }

    void setIndex( final int index )
    {
        this.index = index;
    }

}
