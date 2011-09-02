package org.commonjava.web.maven.proxy.model;

import static org.commonjava.auth.couch.util.IdUtils.namespaceId;

import org.commonjava.couch.model.AbstractCouchDocument;

import com.google.gson.annotations.SerializedName;

public class Proxy
    extends AbstractCouchDocument
{
    public static final String NAMESPACE = "proxy";

    private String name;

    @SerializedName( "remote_url" )
    private String remoteUrl;

    Proxy()
    {}

    public Proxy( final String name, final String remoteUrl )
    {
        this.name = name;
        this.remoteUrl = remoteUrl;
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

    public String getRemoteUrl()
    {
        return remoteUrl;
    }

    public void setRemoteUrl( final String remoteUrl )
    {
        this.remoteUrl = remoteUrl;
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
        Proxy other = (Proxy) obj;
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
        return String.format( "Repository [name=%s, remoteUrl=%s]", name, remoteUrl );
    }

}
