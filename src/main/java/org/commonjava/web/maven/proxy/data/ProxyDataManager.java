package org.commonjava.web.maven.proxy.data;

import static org.commonjava.couch.util.IdUtils.namespaceId;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.commonjava.auth.couch.data.UserDataException;
import org.commonjava.auth.couch.data.UserDataManager;
import org.commonjava.auth.couch.model.Permission;
import org.commonjava.couch.conf.CouchDBConfiguration;
import org.commonjava.couch.db.CouchDBException;
import org.commonjava.couch.db.CouchManager;
import org.commonjava.couch.model.CouchDocRef;
import org.commonjava.couch.model.DenormalizationException;
import org.commonjava.couch.util.JoinString;
import org.commonjava.web.maven.proxy.conf.ProxyConfiguration;
import org.commonjava.web.maven.proxy.data.ProxyAppDescription.View;
import org.commonjava.web.maven.proxy.model.Group;
import org.commonjava.web.maven.proxy.model.Repository;

@Singleton
public class ProxyDataManager
{

    @Inject
    private UserDataManager userMgr;

    @Inject
    private CouchManager couch;

    @Inject
    private ProxyConfiguration config;

    @Inject
    private CouchDBConfiguration couchConfig;

    public ProxyDataManager()
    {}

    public ProxyDataManager( final ProxyConfiguration config, final UserDataManager userMgr,
                             final CouchDBConfiguration couchConfig, final CouchManager couch )
    {
        this.config = config;
        this.userMgr = userMgr;
        this.couchConfig = couchConfig;
        this.couch = couch;
    }

    public Repository getRepository( final String name )
        throws ProxyDataException
    {
        try
        {
            return couch.getDocument( new CouchDocRef( namespaceId( Repository.NAMESPACE, name ) ),
                                      Repository.class );
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
                                      Group.class );
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
                                         Group.class );
        }
        catch ( CouchDBException e )
        {
            throw new ProxyDataException( "Failed to retrieve proxy-group listing. Reason: %s", e,
                                          e.getMessage() );
        }
    }

    public List<Repository> getAllRepositories()
        throws ProxyDataException
    {
        try
        {
            return couch.getViewListing( new ProxyViewRequest( config, View.ALL_REPOSITORIES ),
                                         Repository.class );
        }
        catch ( CouchDBException e )
        {
            throw new ProxyDataException( "Failed to retrieve proxy listing. Reason: %s", e,
                                          e.getMessage() );
        }
    }

    public List<Repository> getRepositoriesForGroup( final String groupName )
        throws ProxyDataException
    {
        try
        {
            return couch.getViewListing( new ProxyViewRequest( config, View.GROUP_REPOSITORIES,
                                                               groupName ), Repository.class );
        }
        catch ( CouchDBException e )
        {
            throw new ProxyDataException( "Failed to retrieve proxies in group: %s. Reason: %s", e,
                                          groupName, e.getMessage() );
        }
    }

    public boolean storeRepository( final Repository proxy )
        throws ProxyDataException
    {
        return storeRepository( proxy, false );
    }

    public boolean storeRepository( final Repository repository, final boolean skipIfExists )
        throws ProxyDataException
    {
        try
        {
            repository.calculateDenormalizedFields();
            boolean result = couch.store( repository, skipIfExists );

            userMgr.createPermissions( Repository.NAMESPACE, repository.getName(),
                                       Permission.ADMIN, Permission.READ );

            return result;
        }
        catch ( CouchDBException e )
        {
            throw new ProxyDataException(
                                          "Failed to store repository configuration: %s. Reason: %s",
                                          e, repository.getName(), e.getMessage() );
        }
        catch ( DenormalizationException e )
        {
            throw new ProxyDataException(
                                          "Failed to store repository configuration: %s. Reason: %s",
                                          e, repository.getName(), e.getMessage() );
        }
        catch ( UserDataException e )
        {
            throw new ProxyDataException(
                                          "Failed to create permissions for repository: %s. Reason: %s",
                                          e, repository.getName(), e.getMessage() );
        }
    }

    public boolean storeGroup( final Group group )
        throws ProxyDataException
    {
        return storeGroup( group, false );
    }

    public boolean storeGroup( final Group group, final boolean skipIfExists )
        throws ProxyDataException
    {
        try
        {
            group.calculateDenormalizedFields();

            Set<String> missing = new HashSet<String>();
            for ( String repoName : group.getConstituents() )
            {
                if ( !couch.exists( new CouchDocRef( namespaceId( Repository.NAMESPACE, repoName ) ) ) )
                {
                    missing.add( repoName );
                }
            }

            if ( !missing.isEmpty() )
            {
                throw new ProxyDataException(
                                              "Invalid repository-group configuration: %s. Reason: One or more constituent repositories are missing: %s",
                                              group.getName(), new JoinString( ", ", missing ) );
            }

            boolean result = couch.store( group, skipIfExists );

            userMgr.createPermissions( Group.NAMESPACE, group.getName(), Permission.ADMIN,
                                       Permission.READ );

            return result;
        }
        catch ( CouchDBException e )
        {
            throw new ProxyDataException(
                                          "Failed to store proxy-group configuration: %s. Reason: %s",
                                          e, group.getName(), e.getMessage() );
        }
        catch ( DenormalizationException e )
        {
            throw new ProxyDataException( "Failed to store group configuration: %s. Reason: %s", e,
                                          group.getName(), e.getMessage() );
        }
        catch ( UserDataException e )
        {
            throw new ProxyDataException( "Failed to create permissions for group: %s. Reason: %s",
                                          e, group.getName(), e.getMessage() );
        }
    }

    public void deleteRepository( final Repository proxy )
        throws ProxyDataException
    {
        try
        {
            couch.delete( proxy );
        }
        catch ( CouchDBException e )
        {
            throw new ProxyDataException( "Failed to delete proxy configuration: %s. Reason: %s",
                                          e, proxy.getName(), e.getMessage() );
        }
    }

    public void deleteRepository( final String name )
        throws ProxyDataException
    {
        try
        {
            couch.delete( new CouchDocRef( namespaceId( Repository.NAMESPACE, name ) ) );
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
            couch.delete( group );
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
            couch.delete( new CouchDocRef( namespaceId( Group.NAMESPACE, name ) ) );
        }
        catch ( CouchDBException e )
        {
            throw new ProxyDataException(
                                          "Failed to delete proxy-group configuration: %s. Reason: %s",
                                          e, name, e.getMessage() );
        }
    }

    public void install()
        throws ProxyDataException
    {
        ProxyAppDescription description = new ProxyAppDescription();

        try
        {
            couch.initialize( description );

            userMgr.install();
            userMgr.setupAdminInformation();

            userMgr.storePermission( new Permission( Repository.NAMESPACE, Permission.ADMIN ) );
            userMgr.storePermission( new Permission( Group.NAMESPACE, Permission.ADMIN ) );
            userMgr.storePermission( new Permission( Repository.NAMESPACE, Permission.READ ) );
            userMgr.storePermission( new Permission( Group.NAMESPACE, Permission.READ ) );
        }
        catch ( CouchDBException e )
        {
            throw new ProxyDataException(
                                          "Failed to initialize proxy-management database: %s (application: %s). Reason: %s",
                                          e, couchConfig.getDatabaseUrl(),
                                          description.getAppName(), e.getMessage() );
        }
        catch ( UserDataException e )
        {
            throw new ProxyDataException(
                                          "Failed to initialize admin user/privilege information in proxy-management database: %s. Reason: %s",
                                          e, couchConfig.getDatabaseUrl(), e.getMessage() );
        }
    }
}
