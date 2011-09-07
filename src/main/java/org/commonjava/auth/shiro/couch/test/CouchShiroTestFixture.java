package org.commonjava.auth.shiro.couch.test;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.UnavailableSecurityManagerException;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.subject.support.SubjectThreadState;
import org.apache.shiro.util.LifecycleUtils;
import org.apache.shiro.util.ThreadState;
import org.commonjava.auth.couch.conf.UserManagerConfiguration;
import org.commonjava.auth.couch.data.PasswordManager;
import org.commonjava.auth.couch.data.UserDataManager;
import org.commonjava.auth.shiro.couch.CouchPermissionResolver;
import org.commonjava.auth.shiro.couch.CouchRealm;
import org.commonjava.couch.db.CouchManager;
import org.commonjava.couch.model.io.CouchAppReader;
import org.commonjava.couch.model.io.Serializer;

public final class CouchShiroTestFixture
{

    private static ThreadState subjectThreadState;

    private CouchShiroTestFixture()
    {}

    public static void setupSecurityManager( final UserManagerConfiguration config,
                                             final Realm... fallbackRealms )
    {
        CouchManager couch = new CouchManager( new Serializer(), new CouchAppReader() );
        UserDataManager mgr = new UserDataManager( config, new PasswordManager(), couch );

        CouchRealm realm = new CouchRealm( mgr, new CouchPermissionResolver( mgr ) );
        realm.setupSecurityManager( fallbackRealms );
    }

    public static void setupSecurityManager( final CouchRealm realm, final Realm... fallbackRealms )
    {
        realm.setupSecurityManager( fallbackRealms );
    }

    public static void teardownSecurityManager()
    {
        clearSubject();

        try
        {
            org.apache.shiro.mgt.SecurityManager securityManager =
                SecurityUtils.getSecurityManager();

            LifecycleUtils.destroy( securityManager );
        }
        catch ( UnavailableSecurityManagerException e )
        {
            // we don't care about this when cleaning up the test environment
        }

        SecurityUtils.setSecurityManager( null );
    }

    public static void setSubject( final Subject subject )
    {
        clearSubject();
        subjectThreadState = new SubjectThreadState( subject );
        subjectThreadState.bind();
    }

    public static void clearSubject()
    {
        if ( subjectThreadState != null )
        {
            subjectThreadState.clear();
            subjectThreadState = null;
        }
    }

}
