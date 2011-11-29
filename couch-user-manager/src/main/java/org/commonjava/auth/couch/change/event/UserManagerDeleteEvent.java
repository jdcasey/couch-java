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
