package org.commonjava.auth.shiro.couch.model.factory;

import java.lang.reflect.Type;

import org.commonjava.auth.couch.model.factory.PermissionCreator;
import org.commonjava.auth.shiro.couch.model.ShiroPermission;

public class ShiroPermissionCreator
    implements PermissionCreator<ShiroPermission>
{

    @Override
    public ShiroPermission createInstance( final Type type )
    {
        return new ShiroPermission();
    }

    @Override
    public Type typeLiteral()
    {
        return ShiroPermission.class;
    }

}
