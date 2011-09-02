package org.commonjava.web.maven.proxy.model;

import static org.commonjava.auth.couch.util.IdUtils.namespaceId;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.commonjava.couch.model.AbstractCouchDocument;

public class Group
    extends AbstractCouchDocument
{

    public static final String NAMESPACE = "group";

    private String name;

    private List<String> constituents;

    public Group( final String name, final List<String> constituents )
    {
        this.name = name;
        this.constituents = constituents;
        setCouchDocId( namespaceId( NAMESPACE, name ) );
    }

    public Group( final String name, final String... constituents )
    {
        this.name = name;
        this.constituents = new ArrayList<String>( Arrays.asList( constituents ) );
        setCouchDocId( namespaceId( NAMESPACE, name ) );
    }

    public Group( final String name, final Proxy... constituents )
    {
        this.name = name;
        setConstituentProxies( Arrays.asList( constituents ) );
        setCouchDocId( namespaceId( NAMESPACE, name ) );
    }

    public String getName()
    {
        return name;
    }

    void setName( final String name )
    {
        this.name = name;
    }

    public List<String> getConstituents()
    {
        return constituents;
    }

    public boolean addConstituent( final Proxy repository )
    {
        if ( repository == null )
        {
            return false;
        }

        return addConstituent( repository.getName() );
    }

    public synchronized boolean addConstituent( final String repository )
    {
        if ( constituents == null )
        {
            constituents = new ArrayList<String>();
        }

        return constituents.add( repository );
    }

    public void setConstituents( final List<String> constituents )
    {
        this.constituents = constituents;
    }

    public void setConstituentProxies( final List<Proxy> constituents )
    {
        this.constituents = null;
        for ( Proxy proxy : constituents )
        {
            addConstituent( proxy );
        }
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ( ( name == null ) ? 0 : name.hashCode() );
        return result;
    }

    @Override
    public boolean equals( final Object obj )
    {
        if ( this == obj )
        {
            return true;
        }
        if ( !super.equals( obj ) )
        {
            return false;
        }
        if ( getClass() != obj.getClass() )
        {
            return false;
        }
        Group other = (Group) obj;
        if ( name == null )
        {
            if ( other.name != null )
            {
                return false;
            }
        }
        else if ( !name.equals( other.name ) )
        {
            return false;
        }
        return true;
    }

    @Override
    public String toString()
    {
        return String.format( "Group [name=%s, constituents=%s]", name, constituents );
    }

}
