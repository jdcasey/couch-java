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
import javax.persistence.EntityManager;
import javax.persistence.TransactionRequiredException;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import javax.transaction.UserTransaction;

import org.commonjava.util.logging.Logger;
import org.commonjava.web.fd.data.WorkspaceDataManager.WorkspaceRepository;
import org.commonjava.web.fd.model.Workspace;

@WebListener
public class DataSeeder
    implements ServletContextListener
{
    private final Logger logger = new Logger( getClass() );

    @Inject
    @WorkspaceRepository
    private EntityManager em;

    @Inject
    private UserTransaction tx;

    @Override
    public void contextInitialized( final ServletContextEvent sce )
    {
        logger.info( "\n\n\n\nImporting seed data...\n\n\n\n" );

        final Workspace ws = new Workspace();
        ws.setId( (long) 1 );
        ws.setName( "Workspace One" );
        ws.setPathName( "workspace-one" );
        try
        {
            try
            {
                em.persist( ws );
            }
            catch ( final TransactionRequiredException e )
            {
                // manual transaction control required in @PostConstruct method
                // only use if enforced by JPA provider (due to bug in GlassFish)
                tx.begin();
                em.persist( ws );
                tx.commit();
            }

            logger.info( "\n\n\n\nSuccessfully imported seed data.\n\n\n\n" );
        }
        catch ( final Exception e )
        {
            logger.error( "Seed data import failed: %s", e, e.getMessage() );
        }
    }

    @Override
    public void contextDestroyed( final ServletContextEvent sce )
    {
    }
}
