/*******************************************************************************
 * Copyright (C) 2011  John Casey
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public
 * License along with this program.  If not, see 
 * <http://www.gnu.org/licenses/>.
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
