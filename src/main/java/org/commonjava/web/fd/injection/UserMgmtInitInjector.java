/*******************************************************************************
 * Copyright (C) 2011 John Casey.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package org.commonjava.web.fd.injection;

import javax.inject.Inject;
import javax.servlet.ServletContextEvent;

import org.commonjava.util.logging.Logger;
import org.commonjava.web.user.data.UserDataException;
import org.commonjava.web.user.data.UserManagerInitializer;

//@WebListener
//@Singleton
public class UserMgmtInitInjector
// implements ServletContextListener
{
    private final Logger logger = new Logger( getClass() );

    private boolean finished = false;

    @Inject
    private UserManagerInitializer initializer;

    // @Override
    public void contextInitialized( final ServletContextEvent sce )
    {
        if ( finished )
        {
            return;
        }

        try
        {
            initializer.initializeAdmin();
        }
        catch ( final UserDataException e )
        {
            logger.error( "Failed to initialize admin-level access: %s", e, e.getMessage() );
        }

        finished = true;
    }

    // @Override
    public void contextDestroyed( final ServletContextEvent sce )
    {
    }
}
