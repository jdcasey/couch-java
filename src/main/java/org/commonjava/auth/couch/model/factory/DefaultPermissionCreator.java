package org.commonjava.auth.couch.model.factory;

import java.lang.reflect.Type;

import org.commonjava.auth.couch.model.Permission;

public class DefaultPermissionCreator
    implements PermissionCreator<Permission>
{

    @Override
    public Permission createInstance( final Type type )
    {
        return new Permission();
    }

    @Override
    public Type typeLiteral()
    {
        return Permission.class;
    }

}
