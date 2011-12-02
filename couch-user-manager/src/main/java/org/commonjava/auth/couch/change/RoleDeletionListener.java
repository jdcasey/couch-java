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
package org.commonjava.auth.couch.change;

import static org.commonjava.couch.util.IdUtils.nonNamespaceId;

import java.util.Set;

import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.commonjava.auth.couch.change.event.UserManagerDeleteEvent;
import org.commonjava.auth.couch.change.event.UserManagerDeleteEvent.Type;
import org.commonjava.auth.couch.data.UserDataException;
import org.commonjava.auth.couch.data.UserDataManager;
import org.commonjava.couch.change.CouchDocChange;
import org.commonjava.couch.change.dispatch.CouchChangeJ2EEEvent;
import org.commonjava.couch.change.dispatch.ThreadableListener;
import org.commonjava.couch.rbac.Role;
import org.commonjava.couch.rbac.User;
import org.commonjava.couch.util.ChangeSynchronizer;
import org.commonjava.util.logging.Logger;

@Singleton
public class RoleDeletionListener
    implements ThreadableListener
{

    private final Logger logger = new Logger( getClass() );

    @Inject
    private UserDataManager dataManager;

    private final ChangeSynchronizer changeSync = new ChangeSynchronizer();

    @Override
    public boolean canProcess( final String id, final boolean deleted )
    {
        return deleted && id.startsWith( Role.NAMESPACE );
    }

    @Override
    public void documentChanged( final CouchDocChange change )
    {
        final String role = nonNamespaceId( Role.NAMESPACE, change.getId() );
        processRemoved( role );
    }

    private void processRemoved( final String role )
    {
        try
        {
            final Set<User> users = dataManager.getUsersForRole( role );
            for ( final User user : users )
            {
                user.removeRole( role );
            }

            dataManager.storeUsers( users );
            changeSync.setChanged();
        }
        catch ( final UserDataException e )
        {
            logger.error( "Failed to update users for deleted role: %s. Error: %s", e, role, e.getMessage() );
        }
    }

    public void roleDeleted( @Observes final CouchChangeJ2EEEvent event )
    {
        final CouchDocChange change = event.getChange();
        if ( canProcess( change.getId(), change.isDeleted() ) )
        {
            documentChanged( change );
        }
    }

    public void roleDeleted( @Observes final UserManagerDeleteEvent event )
    {
        final Type type = event.getType();
        if ( Type.ROLE == type )
        {
            for ( final String perm : event )
            {
                processRemoved( perm );
            }
        }
    }

    @Override
    public void waitForChange( final long totalMillis, final long pollingMillis )
    {
        changeSync.waitForChange( totalMillis, pollingMillis );
    }

}
