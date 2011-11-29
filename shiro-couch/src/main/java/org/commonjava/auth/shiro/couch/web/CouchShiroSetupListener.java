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
package org.commonjava.auth.shiro.couch.web;

import javax.inject.Inject;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.commonjava.auth.shiro.couch.CouchRealm;

/**
 * NOTE: This class is NOT annotated with @WebListener ({@link WebListener}) because the application implementation
 * should have control over whether this is used! To use, subclass this class, and add the annotation to that.
 * 
 * @author jdcasey
 */
public class CouchShiroSetupListener
    implements ServletContextListener
{

    @Inject
    private CouchRealm realm;

    @Override
    public void contextInitialized( final ServletContextEvent sce )
    {
        if ( realm == null )
        {
            throw new RuntimeException(
                                        "Failed to initialize security. Realm has not been injected!" );
        }

        realm.setupSecurityManager();
    }

    @Override
    public void contextDestroyed( final ServletContextEvent sce )
    {
        // NOP
    }

}
