package org.commonjava.maven.mdd.model;

import org.apache.maven.mae.project.key.FullProjectKey;
import org.apache.maven.model.Dependency;

import com.google.gson.annotations.SerializedName;

public class DependencyRelationship
{
    private static final String DEPENDENCY_RELATIONSHIP_DOCTYPE = "dependency-relationship";

    private Artifact dependency;

    private Artifact dependent;

    private String type;

    private String scope;

    @SerializedName( "_id" )
    private String id;

    @SerializedName( "_rev" )
    private String rev;

    @SerializedName( "doctype" )
    private String doctype = DEPENDENCY_RELATIONSHIP_DOCTYPE;

    DependencyRelationship()
    {}

    public DependencyRelationship( final Artifact dependency, final Artifact dependent,
                                   final String type, final String scope )
    {
        this.dependency = dependency;
        this.dependent = dependent;
        this.type = type;
        this.scope = scope == null ? "compile" : scope;
        generateId();
    }

    public DependencyRelationship( final Dependency dep, final FullProjectKey projectKey )
    {
        this.dependency = new Artifact( dep );
        this.dependent = new Artifact( projectKey );
        this.scope = dep.getScope() == null ? "compile" : dep.getScope();
        this.type = dep.getType();
        generateId();
    }

    public DependencyRelationship( final FullProjectKey dependent, final FullProjectKey dependency )
    {
        this.dependent = new Artifact( dependent );
        this.dependency = new Artifact( dependency );
        this.type = null;
        this.scope = null;
        generateId();
    }

    public DependencyRelationship( final Artifact dependency, final Artifact dependent )
    {
        this( dependency, dependent, "jar", null );
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

    String getDoctype()
    {
        return doctype;
    }

    void setDoctype( final String doctype )
    {
        this.doctype = doctype;
    }

}
