package org.commonjava.auth.couch.change.event;

import java.util.Collection;

import org.commonjava.auth.couch.model.User;
import org.commonjava.couch.change.j2ee.AbstractUpdateEvent;

public final class UserUpdateEvent
    extends AbstractUpdateEvent<User>
{

    private final UpdateType type;

    public UserUpdateEvent( final UpdateType type, final Collection<User> users )
    {
        super( users );
        this.type = type;
    }

    public UserUpdateEvent( final UpdateType type, final User... users )
    {
        super( users );
        this.type = type;
    }

    public UpdateType getType()
    {
        return type;
    }

}
