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
public class UserDatabaseProviders
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
    @UserDatabase
    @Default
    public synchronized CouchChangeListener getUserChangeListener()
    {
        System.out.println( "Returning change listener for user DB" );
        if ( changeListener == null )
        {
            changeListener =
                new CouchChangeListener( dispatcher, getUserHttpClient(),
                                         userConfig.getUserDatabaseConfig(), getUserCouchManager(),
                                         serializer );
        }

        return changeListener;
    }

    @Produces
    @UserDatabase
    @Default
    public synchronized CouchManager getUserCouchManager()
    {
        if ( couchManager == null )
        {
            couchManager =
                new CouchManager( userConfig.getUserDatabaseConfig(), getUserHttpClient(),
                                  serializer, appReader );
        }

        return couchManager;
    }

    @Produces
    @UserDatabase
    @Default
    public synchronized CouchHttpClient getUserHttpClient()
    {
        if ( httpClient == null )
        {
            httpClient = new CouchHttpClient( userConfig.getUserDatabaseConfig(), serializer );
        }

        return httpClient;
    }

}
