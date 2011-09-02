package org.commonjava.web.maven.proxy.data;

import static org.commonjava.auth.couch.util.IdUtils.namespaceId;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.commonjava.auth.couch.data.UserDataException;
import org.commonjava.auth.couch.data.UserDataManager;
import org.commonjava.auth.couch.model.Permission;
import org.commonjava.couch.db.CouchDBException;
import org.commonjava.couch.db.CouchManager;
import org.commonjava.couch.db.action.DeleteAction;
import org.commonjava.couch.model.CouchDocRef;
import org.commonjava.couch.util.JoinString;
import org.commonjava.web.maven.proxy.conf.ProxyConfiguration;
import org.commonjava.web.maven.proxy.data.ProxyViewRequest.View;
import org.commonjava.web.maven.proxy.model.Group;
import org.commonjava.web.maven.proxy.model.Proxy;

@Singleton
public class ProxyDataManager
{

    @Inject
    private UserDataManager userMgr;

    @Inject
    private CouchManager couch;

    @Inject
    private ProxyConfiguration config;

    public ProxyDataManager()
    {}

    public ProxyDataManager( final ProxyConfiguration config, final UserDataManager userMgr,
                             final CouchManager couch )
    {
        this.config = config;
        this.userMgr = userMgr;
        this.couch = couch;
    }

    public Proxy getProxy( final String name )
        throws ProxyDataException
    {
        try
        {
            return couch.getDocument( new CouchDocRef( namespaceId( Proxy.NAMESPACE, name ) ),
                                      config.getDatabaseUrl(), Proxy.class );
        }
        catch ( CouchDBException e )
        {
            throw new ProxyDataException( "Failed to retrieve proxy: %s. Reason: %s", e, name,
                                          e.getMessage() );
        }
    }

    public Group getGroup( final String name )
        throws ProxyDataException
    {
        try
        {
            return couch.getDocument( new CouchDocRef( namespaceId( Group.NAMESPACE, name ) ),
                                      config.getDatabaseUrl(), Group.class );
        }
        catch ( CouchDBException e )
        {
            throw new ProxyDataException( "Failed to retrieve proxy-group: %s. Reason: %s", e,
                                          name, e.getMessage() );
        }
    }

    public List<Group> getAllGroups()
        throws ProxyDataException
    {
        try
        {
            return couch.getViewListing( new ProxyViewRequest( config, View.ALL_GROUPS ),
                                         config.getDatabaseUrl(), Group.class );
        }
        catch ( CouchDBException e )
        {
            throw new ProxyDataException( "Failed to retrieve proxy-group listing. Reason: %s", e,
                                          e.getMessage() );
        }
    }

    public List<Proxy> getAllProxies()
        throws ProxyDataException
    {
        try
        {
            return couch.getViewListing( new ProxyViewRequest( config, View.ALL_PROXIES ),
                                         config.getDatabaseUrl(), Proxy.class );
        }
        catch ( CouchDBException e )
        {
            throw new ProxyDataException( "Failed to retrieve proxy listing. Reason: %s", e,
                                          e.getMessage() );
        }
    }

    public List<Proxy> getProxiesForGroup( final String groupName )
        throws ProxyDataException
    {
        try
        {
            return couch.getViewListing( new ProxyViewRequest( config, View.GROUP_REPOSITORIES,
                                                               groupName ), groupName, Proxy.class );
        }
        catch ( CouchDBException e )
        {
            throw new ProxyDataException( "Failed to retrieve proxies in group: %s. Reason: %s", e,
                                          groupName, e.getMessage() );
        }
    }

    public void storeProxy( final Proxy proxy )
        throws ProxyDataException
    {
        try
        {
            couch.store( proxy, config.getDatabaseUrl(), false );
        }
        catch ( CouchDBException e )
        {
            throw new ProxyDataException( "Failed to store proxy configuration: %s. Reason: %s", e,
                                          proxy.getName(), e.getMessage() );
        }
    }

    public void storeGroup( final Group group )
        throws ProxyDataException
    {
        try
        {
            couch.store( group, config.getDatabaseUrl(), false );
        }
        catch ( CouchDBException e )
        {
            throw new ProxyDataException(
                                          "Failed to store proxy-group configuration: %s. Reason: %s",
                                          e, group.getName(), e.getMessage() );
        }
    }

    public void deleteProxy( final Proxy proxy )
        throws ProxyDataException
    {
        try
        {
            couch.delete( proxy, config.getDatabaseUrl() );
        }
        catch ( CouchDBException e )
        {
            throw new ProxyDataException( "Failed to delete proxy configuration: %s. Reason: %s",
                                          e, proxy.getName(), e.getMessage() );
        }
    }

    public void deleteProxy( final String name )
        throws ProxyDataException
    {
        try
        {
            couch.delete( new CouchDocRef( namespaceId( Proxy.NAMESPACE, name ) ),
                          config.getDatabaseUrl() );
        }
        catch ( CouchDBException e )
        {
            throw new ProxyDataException( "Failed to delete proxy configuration: %s. Reason: %s",
                                          e, name, e.getMessage() );
        }
    }

    public void deleteGroup( final Group group )
        throws ProxyDataException
    {
        try
        {
            couch.delete( group, config.getDatabaseUrl() );
        }
        catch ( CouchDBException e )
        {
            throw new ProxyDataException(
                                          "Failed to delete proxy-group configuration: %s. Reason: %s",
                                          e, group.getName(), e.getMessage() );
        }
    }

    public void deleteGroup( final String name )
        throws ProxyDataException
    {
        try
        {
            couch.delete( new CouchDocRef( namespaceId( Group.NAMESPACE, name ) ),
                          config.getDatabaseUrl() );
        }
        catch ( CouchDBException e )
        {
            throw new ProxyDataException(
                                          "Failed to delete proxy-group configuration: %s. Reason: %s",
                                          e, name, e.getMessage() );
        }
    }

    public void deleteGroupAndConstituents( final Group group )
        throws ProxyDataException
    {
        deleteGroupAndConstituents( group.getName() );
    }

    public void deleteGroupAndConstituents( final String name )
        throws ProxyDataException
    {
        List<Proxy> proxies = getProxiesForGroup( name );
        List<DeleteAction> actions = new ArrayList<DeleteAction>();
        actions.add( new DeleteAction( namespaceId( Group.NAMESPACE, name ) ) );
        for ( Proxy proxy : proxies )
        {
            actions.add( new DeleteAction( proxy.getCouchDocId() ) );
        }

        try
        {
            couch.modify( actions, config.getDatabaseUrl(), true );
        }
        catch ( CouchDBException e )
        {
            throw new ProxyDataException(
                                          "Failed to delete group: %s along with all of its proxies: %s.\nReason: %s",
                                          e, name, new JoinString( ", ", proxies ), e.getMessage() );
        }
    }

    public void install()
        throws ProxyDataException
    {
        try
        {
            couch.initialize( config.getDatabaseUrl(), config.getLogicApplication(),
                              ProxyViewRequest.APPLICATION_RESOURCE );

            userMgr.install();
            userMgr.setupAdminInformation();

            userMgr.storePermission( new Permission( Proxy.NAMESPACE, Permission.ADMIN ) );
            userMgr.storePermission( new Permission( Group.NAMESPACE, Permission.ADMIN ) );
        }
        catch ( CouchDBException e )
        {
            throw new ProxyDataException(
                                          "Failed to initialize proxy-management database: %s (application: %s). Reason: %s",
                                          e, config.getDatabaseUrl(),
                                          ProxyViewRequest.APPLICATION_RESOURCE, e.getMessage() );
        }
        catch ( UserDataException e )
        {
            throw new ProxyDataException(
                                          "Failed to initialize admin user/privilege information in proxy-management database: %s. Reason: %s",
                                          e, config.getDatabaseUrl(), e.getMessage() );
        }
    }
}
