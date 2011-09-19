package org.commonjava.auth.couch.change;

import static org.commonjava.couch.util.IdUtils.nonNamespaceId;

import java.util.Set;

import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.commonjava.auth.couch.data.UserDataException;
import org.commonjava.auth.couch.data.UserDataManager;
import org.commonjava.auth.couch.model.Role;
import org.commonjava.auth.couch.model.User;
import org.commonjava.couch.change.CouchDocChange;
import org.commonjava.couch.change.dispatch.CouchChangeJ2EEEvent;
import org.commonjava.couch.change.dispatch.ThreadableListener;
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
        String role = nonNamespaceId( Role.NAMESPACE, change.getId() );
        try
        {
            Set<User> users = dataManager.getUsersForRole( role );
            for ( User user : users )
            {
                user.removeRole( role );
            }

            dataManager.storeUsers( users );
            changeSync.setChanged();
        }
        catch ( UserDataException e )
        {
            logger.error( "Failed to update users for deleted role: %s. Error: %s", e, role,
                          e.getMessage() );
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
    public void waitForChange( final long totalMillis, final long pollingMillis )
    {
        changeSync.waitForChange( totalMillis, pollingMillis );
    }

}
