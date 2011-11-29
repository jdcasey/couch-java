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
import org.commonjava.couch.conf.CouchDBConfiguration;
import org.commonjava.couch.db.CouchManager;
import org.commonjava.couch.io.CouchAppReader;
import org.commonjava.couch.io.CouchHttpClient;
import org.commonjava.couch.io.Serializer;

public final class CouchShiroTestFixture
{

    private static ThreadState subjectThreadState;

    private CouchShiroTestFixture()
    {}

    public static void setupSecurityManager( final CouchDBConfiguration couchConfig,
                                             final UserManagerConfiguration userConfig,
                                             final Realm... fallbackRealms )
    {
        Serializer serializer = new Serializer();
        CouchManager couch =
            new CouchManager( couchConfig, new CouchHttpClient( couchConfig, serializer ),
                              serializer, new CouchAppReader() );

        UserDataManager mgr = new UserDataManager( userConfig, new PasswordManager(), couch );

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
