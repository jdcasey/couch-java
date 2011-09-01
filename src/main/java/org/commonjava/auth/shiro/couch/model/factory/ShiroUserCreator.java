package org.commonjava.auth.shiro.couch.model.factory;

import java.lang.reflect.Type;

import org.commonjava.auth.couch.model.factory.UserCreator;
import org.commonjava.auth.shiro.couch.model.ShiroUser;

public class ShiroUserCreator
    implements UserCreator<ShiroUser>
{

    @Override
    public ShiroUser createInstance( final Type type )
    {
        return new ShiroUser();
    }

    @Override
    public Type typeLiteral()
    {
        return ShiroUser.class;
    }

}
