/*******************************************************************************
 * Copyright 2011 John Casey
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
