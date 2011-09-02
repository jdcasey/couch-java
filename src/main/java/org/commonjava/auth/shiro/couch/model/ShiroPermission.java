package org.commonjava.auth.shiro.couch.model;

import org.commonjava.auth.couch.model.Permission;

public class ShiroPermission
    implements org.apache.shiro.authz.Permission
{

    private final Permission permission;

    public ShiroPermission( final Permission permission )
    {
        this.permission = permission;
    }

    @Override
    public boolean implies( final org.apache.shiro.authz.Permission p )
    {
        String name = permission.getName();
        if ( name.equals( Permission.WILDCARD ) )
        {
            return true;
        }

        if ( name.endsWith( Permission.WILDCARD ) && ( p instanceof ShiroPermission ) )
        {
            ShiroPermission perm = (ShiroPermission) p;
            String prefix = name.substring( 0, name.length() - Permission.WILDCARD.length() );

            String permName = perm.permission.getName();
            return permName.length() > prefix.length() && permName.startsWith( prefix );
        }

        return false;
    }

}
