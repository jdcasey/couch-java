package org.commonjava.auth.couch.model.factory;

import java.lang.reflect.Type;

import org.commonjava.auth.couch.model.Role;

public class DefaultRoleCreator
    implements RoleCreator<Role>
{

    @Override
    public Role createInstance( final Type type )
    {
        return new Role();
    }

    @Override
    public Type typeLiteral()
    {
        return Role.class;
    }

}
