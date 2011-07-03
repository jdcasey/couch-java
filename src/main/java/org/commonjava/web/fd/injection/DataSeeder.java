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
