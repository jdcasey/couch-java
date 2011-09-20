package org.commonjava.auth.couch.change.event;

import java.util.Collection;

import org.commonjava.auth.couch.model.Permission;
import org.commonjava.couch.change.j2ee.AbstractUpdateEvent;

public final class PermissionUpdateEvent
    extends AbstractUpdateEvent<Permission>
{

    private final UpdateType type;

    public PermissionUpdateEvent( final UpdateType type, final Collection<Permission> permissions )
    {
        super( permissions );
        this.type = type;
    }

    public PermissionUpdateEvent( final UpdateType type, final Permission... permissions )
    {
        super( permissions );
        this.type = type;
    }

    public UpdateType getType()
    {
        return type;
    }
}
