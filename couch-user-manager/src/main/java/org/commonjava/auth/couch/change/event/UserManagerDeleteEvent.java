package org.commonjava.auth.couch.change.event;

import java.util.Collection;

import org.commonjava.couch.change.j2ee.AbstractUpdateEvent;

public class UserManagerDeleteEvent
    extends AbstractUpdateEvent<String>
{

    public enum Type
    {
        USER, ROLE, PERMISSION;
    }

    private final Type type;

    public UserManagerDeleteEvent( final Type type, final Collection<String> names )
    {
        super( names );
        this.type = type;
    }

    public UserManagerDeleteEvent( final Type type, final String... names )
    {
        super( names );
        this.type = type;
    }

    public Type getType()
    {
        return type;
    }

}
