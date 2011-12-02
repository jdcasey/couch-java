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
import org.commonjava.couch.rbac.Permission;
import org.commonjava.couch.rbac.Role;
import org.commonjava.couch.util.ChangeSynchronizer;
import org.commonjava.util.logging.Logger;

@Singleton
public class PermissionDeletionListener
    implements ThreadableListener
{

    private final Logger logger = new Logger( getClass() );

    @Inject
    private UserDataManager dataManager;

    private final ChangeSynchronizer changeSync = new ChangeSynchronizer();

    @Override
    public boolean canProcess( final String id, final boolean deleted )
    {
        return deleted && id.startsWith( Permission.NAMESPACE );
    }

    @Override
    public void documentChanged( final CouchDocChange change )
    {
        final String permission = nonNamespaceId( Permission.NAMESPACE, change.getId() );
        if ( processRemoved( permission ) )
        {
            changeSync.setChanged();
        }
    }

    private boolean processRemoved( final String permission )
    {
        boolean changed = false;
        try
        {
            final Set<Role> roles = dataManager.getRolesForPermission( permission );
            for ( final Role role : roles )
            {
                changed = role.removePermission( permission ) || changed;
            }

            dataManager.storeRoles( roles );
        }
        catch ( final UserDataException e )
        {
            logger.error( "Failed to update roles for deleted permission: %s. Error: %s", e, permission, e.getMessage() );
        }
        return changed;
    }

    public void permissionDeleted( @Observes final CouchChangeJ2EEEvent event )
    {
        final CouchDocChange change = event.getChange();
        if ( canProcess( change.getId(), change.isDeleted() ) )
        {
            documentChanged( change );
        }
    }

    public void permissionDeleted( @Observes final UserManagerDeleteEvent event )
    {
        final Type type = event.getType();
        if ( Type.PERMISSION == type )
        {
            boolean changed = false;
            for ( final String perm : event )
            {
                changed = processRemoved( perm ) || changed;
            }

            if ( changed )
            {
                changeSync.setChanged();
            }
        }
    }

    @Override
    public synchronized void waitForChange( final long totalMillis, final long pollingMillis )
    {
        changeSync.waitForChange( totalMillis, pollingMillis );
    }

}
