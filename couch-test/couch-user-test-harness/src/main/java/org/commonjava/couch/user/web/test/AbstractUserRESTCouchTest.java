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
package org.commonjava.couch.user.web.test;

import static org.commonjava.couch.util.UrlUtils.buildUrl;

import java.net.MalformedURLException;

import javax.inject.Inject;

import org.commonjava.auth.couch.conf.UserManagerConfiguration;
import org.commonjava.auth.couch.data.PasswordManager;
import org.commonjava.auth.couch.data.UserDataManager;
import org.commonjava.auth.couch.inject.UserData;
import org.commonjava.auth.shiro.couch.CouchRealm;
import org.commonjava.couch.db.CouchManager;
import org.commonjava.web.common.ser.JsonSerializer;
import org.junit.After;
import org.junit.Before;

public abstract class AbstractUserRESTCouchTest
{
    protected static final String HOST = "localhost";

    protected static final int PORT = 8080;

    @Inject
    protected JsonSerializer serializer;

    @Inject
    protected UserDataManager userManager;

    @Inject
    protected UserManagerConfiguration config;

    @Inject
    protected PasswordManager passwordManager;

    @Inject
    protected CouchRealm realm;

    @Inject
    @UserData
    private CouchManager userCouch;

    protected AbstractUserRESTCouchTest()
    {
    }

    protected abstract CouchManager getCouchManager();

    @Before
    public final void setupRESTCouchTest()
        throws Exception
    {
        getCouchManager().dropDatabase();
        userCouch.dropDatabase();

        userManager.install();
        userManager.setupAdminInformation();

        // setup the security manager.
        realm.setupSecurityManager();
    }

    @After
    public final void teardownRESTCouchTest()
        throws Exception
    {
        // getCouchManager().dropDatabase();
    }

    protected String resourceUrl( final String path )
        throws MalformedURLException
    {
        return buildUrl( "http://localhost:8080/test/api/", apiVersion(), path );
    }

    protected String apiVersion()
    {
        return "1.0";
    }

}
