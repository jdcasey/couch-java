package org.commonjava.auth.couch.model;

import static org.apache.commons.lang.StringUtils.join;
import static org.commonjava.auth.couch.util.IdUtils.namespaceId;

import org.commonjava.couch.model.AbstractCouchDocument;

import com.google.gson.annotations.Expose;

public class Permission
    extends AbstractCouchDocument
    implements org.apache.shiro.authz.Permission

{

    public static final String WILDCARD = "*";

    public static final String CREATE = "create";

    public static final String READ = "read";

    public static final String UPDATE = "update";

    public static final String DELETE = "delete";

    public static final String ADMIN = "admin";

    public static final String NAMESPACE = "permission";

    private String name;

    @Expose( deserialize = false )
    private final String docType = NAMESPACE;

    Permission()
    {}

    public Permission( final String... nameParts )
    {
        this.name = name( nameParts );
        setCouchDocId( namespaceId( NAMESPACE, this.name ) );
    }

    public String getName()
    {
        return name;
    }

    void setName( final String name )
    {
        this.name = name;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + name.hashCode();
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
        final Permission other = (Permission) obj;
        if ( !name.equals( other.name ) )
        {
            return false;
        }
        return true;
    }

    @Override
    public boolean implies( final org.apache.shiro.authz.Permission p )
    {
        if ( name.equals( WILDCARD ) )
        {
            return true;
        }

        if ( name.endsWith( WILDCARD ) && ( p instanceof Permission ) )
        {
            Permission perm = (Permission) p;
            String prefix = name.substring( 0, name.length() - WILDCARD.length() );

            String permName = perm.getName();
            return permName.length() > prefix.length() && permName.startsWith( prefix );
        }

        return false;
    }

    @Override
    public String toString()
    {
        return String.format( "Permission@%d [%s]", hashCode(), name );
    }

    public static String name( final String... parts )
    {
        return join( parts, ":" );
    }

    public String getDocType()
    {
        return docType;
    }

}
