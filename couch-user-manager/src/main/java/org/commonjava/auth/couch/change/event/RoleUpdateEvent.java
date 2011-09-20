/*******************************************************************************
 * Copyright (C) 2011  John Casey
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public
 * License along with this program.  If not, see 
 * <http://www.gnu.org/licenses/>.
 ******************************************************************************/
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
