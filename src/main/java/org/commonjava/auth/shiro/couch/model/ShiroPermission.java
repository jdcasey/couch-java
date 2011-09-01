package org.commonjava.auth.shiro.couch.model;

import org.commonjava.auth.couch.model.Permission;


public class ShiroPermission
    extends Permission
    implements org.apache.shiro.authz.Permission
{

    public ShiroPermission()
    {}

    public ShiroPermission( final String firstPart, final String... nameParts )
    {
        super( firstPart, nameParts );
    }

    @Override
    public boolean implies( final org.apache.shiro.authz.Permission p )
    {
        String name = getName();
        if ( name.equals( WILDCARD ) )
        {
            return true;
        }

        if ( name.endsWith( WILDCARD ) && ( p instanceof ShiroPermission ) )
        {
            ShiroPermission perm = (ShiroPermission) p;
            String prefix = name.substring( 0, name.length() - WILDCARD.length() );

            String permName = perm.getName();
            return permName.length() > prefix.length() && permName.startsWith( prefix );
        }

        return false;
    }

}
