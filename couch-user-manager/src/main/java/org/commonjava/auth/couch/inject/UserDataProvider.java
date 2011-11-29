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
