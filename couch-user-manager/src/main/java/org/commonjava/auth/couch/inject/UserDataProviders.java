package org.commonjava.auth.couch.inject;

import javax.enterprise.inject.Default;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.commonjava.auth.couch.conf.UserManagerConfiguration;
import org.commonjava.couch.change.CouchChangeListener;
import org.commonjava.couch.change.dispatch.CouchChangeDispatcher;
import org.commonjava.couch.db.CouchManager;
import org.commonjava.couch.io.CouchAppReader;
import org.commonjava.couch.io.CouchHttpClient;
import org.commonjava.couch.io.Serializer;

@Singleton
public class UserDataProviders
{

    @Inject
    private Serializer serializer;

    @Inject
    private CouchAppReader appReader;

    @Inject
    private UserManagerConfiguration userConfig;

    @Inject
    private CouchChangeDispatcher dispatcher;

    private CouchManager couchManager;

    private CouchHttpClient httpClient;

    private CouchChangeListener changeListener;

    @Produces
    @UserData
    @Default
    public synchronized CouchChangeListener getChangeListener()
    {
        System.out.println( "Returning change listener for user DB" );
        if ( changeListener == null )
        {
            changeListener =
                new CouchChangeListener( dispatcher, getHttpClient(),
                                         userConfig.getDatabaseConfig(), getCouchManager(),
                                         serializer );
        }

        return changeListener;
    }

    @Produces
    @UserData
    @Default
    public synchronized CouchManager getCouchManager()
    {
        if ( couchManager == null )
        {
            couchManager =
                new CouchManager( userConfig.getDatabaseConfig(), getHttpClient(), serializer,
                                  appReader );
        }

        return couchManager;
    }

    @Produces
    @UserData
    @Default
    public synchronized CouchHttpClient getHttpClient()
    {
        if ( httpClient == null )
        {
            httpClient = new CouchHttpClient( userConfig.getDatabaseConfig(), serializer );
        }

        return httpClient;
    }

}
