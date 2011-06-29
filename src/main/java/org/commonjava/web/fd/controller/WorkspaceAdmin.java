package org.commonjava.web.fd.controller;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Event;
import javax.enterprise.inject.Model;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import org.commonjava.util.logging.Logger;
import org.commonjava.web.fd.data.WorkspaceDataManager.WorkspaceRepository;
import org.commonjava.web.fd.model.Workspace;

@Model
public class WorkspaceAdmin
{

    private final Logger logger = new Logger( getClass() );

    private static final int GENERATED_PATH_MAXLEN = 12;

    @Inject
    @WorkspaceRepository
    private EntityManager em;

    @Inject
    private Event<Workspace> eventSrc;

    @Inject
    private UserTransaction tx;

    @Inject
    private List<Workspace> workspaces;

    private Workspace newWorkspace;

    public List<Workspace> getWorkspaces()
    {
        return workspaces;
    }

    @Produces
    @Named
    public Workspace getNewWorkspace()
    {
        return newWorkspace;
    }

    public void createWorkspace()
        throws NotSupportedException, SystemException, RollbackException, HeuristicMixedException,
        HeuristicRollbackException
    {
        logger.info( "\n\nSaving workspace: %s\n\n", newWorkspace );

        tx.begin();
        em.joinTransaction();
        em.persist( newWorkspace );
        tx.commit();
        eventSrc.fire( newWorkspace );
        createWorkspaceInstance();
    }

    public void generatePathName()
    {
        if ( newWorkspace.getName() != null )
        {
            final StringBuilder sb = new StringBuilder();
            int count = 0;
            for ( final Character c : newWorkspace.getName()
                                                  .toCharArray() )
            {
                if ( count == GENERATED_PATH_MAXLEN )
                {
                    break;
                }
                else if ( !Character.isLetterOrDigit( c ) )
                {
                    sb.append( '-' );
                }
                else if ( Character.isUpperCase( c ) )
                {
                    if ( sb.length() > 0 )
                    {
                        sb.append( '-' );
                    }

                    sb.append( Character.toLowerCase( c ) );
                }
                else
                {
                    sb.append( c );
                }

                count++;
            }

            newWorkspace.setPathName( sb.toString() );
        }
    }

    @PostConstruct
    private void createWorkspaceInstance()
    {
        newWorkspace = new Workspace();
    }

}
