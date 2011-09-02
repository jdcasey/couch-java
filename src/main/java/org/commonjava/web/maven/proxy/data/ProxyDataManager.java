package org.commonjava.web.maven.proxy.data;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.commonjava.auth.couch.data.UserDataManager;
import org.commonjava.couch.db.CouchManager;

@Singleton
public class ProxyDataManager
{

    @Inject
    private UserDataManager userMgr;

    @Inject
    private CouchManager couch;

    public ProxyDataManager()
    {}

    public ProxyDataManager( final UserDataManager userMgr, final CouchManager couch )
    {
        this.userMgr = userMgr;
        this.couch = couch;
    }

}
