package org.commonjava.auth.couch.model.factory;

import java.lang.reflect.Type;

import org.commonjava.auth.couch.model.User;

public class DefaultUserCreator
    implements UserCreator<User>
{

    @Override
    public User createInstance( final Type type )
    {
        return new User();
    }

    @Override
    public Type typeLiteral()
    {
        return User.class;
    }

}
