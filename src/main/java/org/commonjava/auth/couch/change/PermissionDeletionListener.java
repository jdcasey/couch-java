package org.commonjava.auth.couch.change;

import static org.commonjava.couch.util.IdUtils.nonNamespaceId;

import java.util.Set;

import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.commonjava.auth.couch.data.UserDataException;
import org.commonjava.auth.couch.data.UserDataManager;
import org.commonjava.auth.couch.model.Permission;
import org.commonjava.auth.couch.model.Role;
import org.commonjava.couch.change.CouchDocChange;
import org.commonjava.couch.change.dispatch.CouchChangeJ2EEEvent;
import org.commonjava.couch.change.dispatch.ThreadableListener;
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
        String permission = nonNamespaceId( Permission.NAMESPACE, change.getId() );
        try
        {
            Set<Role> roles = dataManager.getRolesForPermission( permission );
            for ( Role role : roles )
            {
                role.removePermission( permission );
            }

            dataManager.storeRoles( roles );

            changeSync.setChanged();
        }
        catch ( UserDataException e )
        {
            logger.error( "Failed to update roles for deleted permission: %s. Error: %s", e,
                          permission, e.getMessage() );
        }
    }

    public void roleDeleted( @Observes final CouchChangeJ2EEEvent event )
    {
        CouchDocChange change = event.getChange();
        if ( canProcess( change.getId(), change.isDeleted() ) )
        {
            documentChanged( change );
        }
    }

    @Override
    public synchronized void waitForChange( final long totalMillis, final long pollingMillis )
    {
        changeSync.waitForChange( totalMillis, pollingMillis );
    }

}
