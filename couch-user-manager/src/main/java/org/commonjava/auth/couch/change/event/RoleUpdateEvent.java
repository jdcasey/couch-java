package org.commonjava.auth.couch.change.event;

import java.util.Collection;

import org.commonjava.auth.couch.model.Role;
import org.commonjava.couch.change.j2ee.AbstractUpdateEvent;

public final class RoleUpdateEvent
    extends AbstractUpdateEvent<Role>
{

    private final UpdateType type;

    public RoleUpdateEvent( final UpdateType type, final Collection<Role> roles )
    {
        super( roles );
        this.type = type;
    }

    public RoleUpdateEvent( final UpdateType type, final Role... roles )
    {
        super( roles );
        this.type = type;
    }

    public UpdateType getType()
    {
        return type;
    }
}
