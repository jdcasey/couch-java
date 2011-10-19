package org.commonjava.auth.couch.inject;

import javax.enterprise.inject.Default;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.commonjava.couch.change.CouchChangeListener;
import org.commonjava.couch.conf.CouchDBConfiguration;
import org.commonjava.couch.db.CouchFactory;
import org.commonjava.couch.db.CouchManager;
import org.commonjava.couch.io.CouchHttpClient;

@Singleton
public class UserDataProvider
{

    @Inject
    private CouchFactory couchFactory;

    @Inject
    @UserData
    private CouchDBConfiguration config;

    @Produces
    @UserData
    @Default
    public CouchManager getCouch()
    {
        return couchFactory.getCouchManager( config );
    }

    @Produces
    @UserData
    @Default
    public CouchHttpClient getHttpClient()
    {
        return couchFactory.getHttpClient( config );
    }

    @Produces
    @UserData
    @Default
    public CouchChangeListener getChangeListener()
    {
        return couchFactory.getChangeListener( config );
    }

}
