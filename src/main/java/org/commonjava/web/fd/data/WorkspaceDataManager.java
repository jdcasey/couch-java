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
package org.commonjava.web.fd.data;

import static org.commonjava.web.user.data.UserDataManager.CREATE;
import static org.commonjava.web.user.data.UserDataManager.READ;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.enterprise.event.Reception;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Qualifier;
import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import org.commonjava.web.fd.model.Workspace;
import org.commonjava.web.user.data.UserDataException;
import org.commonjava.web.user.data.UserDataManager;
import org.commonjava.web.user.model.Permission;
import org.commonjava.web.user.model.Role;

@Singleton
public class WorkspaceDataManager
{
    private static final String WORKSPACE_PERMBASE = "workspace";

    @Inject
    @WorkspaceRepository
    private EntityManager em;

    @Inject
    private Event<Workspace> eventSrc;

    @Inject
    private UserTransaction tx;

    @Inject
    private UserDataManager userMgr;

    private List<Workspace> workspaces;

    public void addWorkspace( final Workspace ws, final boolean autoCommit )
        throws WorkspaceDataException, UserDataException
    {
        try
        {
            if ( autoCommit )
            {
                tx.begin();
            }

            em.joinTransaction();
            em.persist( ws );

            final String name = ws.getPathName();

            final Map<String, Permission> perms = userMgr.createCRUDPermissions( WORKSPACE_PERMBASE, name, false );

            userMgr.saveRole( new Role( name + "-all", perms.values() ), false );
            userMgr.saveRole( new Role( name + "-create_read", perms.get( CREATE ), perms.get( READ ) ), false );
            userMgr.saveRole( new Role( name + "-read", perms.get( READ ) ), false );

            if ( autoCommit )
            {
                tx.commit();
            }

            loadAllWorkspacesOrderedByName();
            eventSrc.fire( ws );
        }
        catch ( final NotSupportedException e )
        {
            throw new WorkspaceDataException( "Cannot save workspace: %s. Error: %s", e, ws, e.getMessage() );
        }
        catch ( final SystemException e )
        {
            throw new WorkspaceDataException( "Cannot save workspace: %s. Error: %s", e, ws, e.getMessage() );
        }
        catch ( final RollbackException e )
        {
            throw new WorkspaceDataException( "Cannot save workspace: %s. Error: %s", e, ws, e.getMessage() );
        }
        catch ( final HeuristicMixedException e )
        {
            throw new WorkspaceDataException( "Cannot save workspace: %s. Error: %s", e, ws, e.getMessage() );
        }
        catch ( final HeuristicRollbackException e )
        {
            throw new WorkspaceDataException( "Cannot save workspace: %s. Error: %s", e, ws, e.getMessage() );
        }
    }

    @Produces
    @Named
    public List<Workspace> getWorkspaces()
    {
        return workspaces;
    }

    public synchronized WorkspaceDataManager reloadWorkspaces()
    {
        loadAllWorkspacesOrderedByName();
        return this;
    }

    public synchronized void onWorkspacesChanged( @Observes( notifyObserver = Reception.ALWAYS ) final Workspace workspace )
    {
        if ( workspaces != null )
        {
            loadAllWorkspacesOrderedByName();
        }
    }

    @PostConstruct
    public synchronized void loadAllWorkspacesOrderedByName()
    {
        final CriteriaBuilder cb = em.getCriteriaBuilder();
        final CriteriaQuery<Workspace> query = cb.createQuery( Workspace.class );
        final Root<Workspace> root = query.from( Workspace.class );

        query.select( root )
             .orderBy( cb.asc( root.get( "name" ) ) );

        workspaces = em.createQuery( query )
                       .getResultList();
    }

    public static final class WorkspaceRepositoryProducer
    {
        @SuppressWarnings( "unused" )
        @Produces
        @WorkspaceRepository
        @PersistenceContext
        private EntityManager em;
    }

    @Qualifier
    @Target( { ElementType.TYPE, ElementType.METHOD, ElementType.PARAMETER, ElementType.FIELD } )
    @Retention( RetentionPolicy.RUNTIME )
    public @interface WorkspaceRepository
    {
    }

}
