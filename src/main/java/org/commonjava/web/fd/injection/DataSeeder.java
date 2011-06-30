package org.commonjava.web.fd.injection;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.TransactionRequiredException;
import javax.servlet.annotation.WebServlet;
import javax.transaction.UserTransaction;

import org.commonjava.util.logging.Logger;
import org.commonjava.web.fd.data.WorkspaceDataManager.WorkspaceRepository;
import org.commonjava.web.fd.model.Workspace;

@Singleton
@WebServlet( loadOnStartup = 0 )
public class DataSeeder
{
    private final Logger logger = new Logger( getClass() );

    @Inject
    @WorkspaceRepository
    private EntityManager em;

    @Inject
    private UserTransaction tx;

    @PostConstruct
    public void importData()
    {
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

            logger.info( "Successfully imported seed data." );
        }
        catch ( final Exception e )
        {
            logger.warn( "Seed data import failed.", e );
        }
    }
}
