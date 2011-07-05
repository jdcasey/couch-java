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
import javax.inject.Singleton;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.commonjava.util.logging.Logger;
import org.commonjava.web.fd.config.FileDepotConfiguration;
import org.commonjava.web.fd.data.WorkspaceDataException;
import org.commonjava.web.fd.data.WorkspaceDataManager;
import org.commonjava.web.fd.model.Workspace;
import org.commonjava.web.user.data.UserDataException;

@WebListener
@Singleton
public class TestDataInjector
    implements ServletContextListener
{
    private final Logger logger = new Logger( getClass() );

    private boolean finished = false;

    @Inject
    private WorkspaceDataManager dataManager;

    @SuppressWarnings( "unused" )
    @Inject
    private FileDepotConfiguration config;

    @Override
    public void contextInitialized( final ServletContextEvent sce )
    {
        if ( finished )
        {
            return;
        }

        logger.info( "\n\n\n\nImporting seed data...\n\n\n\n" );

        final Workspace ws = new Workspace( 1L, "Workspace One", "workspace-one" );
        if ( !dataManager.reloadWorkspaces()
                         .getWorkspaces()
                         .contains( ws ) )
        {
            try
            {
                dataManager.addWorkspace( ws, true );
                logger.info( "\n\n\n\nSuccessfully imported seed data.\n\n\n\n" );
            }
            catch ( final WorkspaceDataException e )
            {
                logger.error( "Seed data import failed: %s", e, e.getMessage() );
            }
            catch ( final UserDataException e )
            {
                logger.error( "Seed data import failed: %s", e, e.getMessage() );
            }
        }

        finished = true;
    }

    @Override
    public void contextDestroyed( final ServletContextEvent sce )
    {
    }
}
