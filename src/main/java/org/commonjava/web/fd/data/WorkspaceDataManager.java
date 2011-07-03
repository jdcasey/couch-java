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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.event.Reception;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Qualifier;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.commonjava.web.fd.model.Workspace;

@RequestScoped
public class WorkspaceDataManager
{
    @Inject
    @WorkspaceRepository
    private EntityManager em;

    private List<Workspace> workspaces;

    @Produces
    @Named
    public List<Workspace> getWorkspaces()
    {
        return workspaces;
    }

    public void onWorkspacesChanged( @Observes( notifyObserver = Reception.IF_EXISTS ) final Workspace workspace )
    {
        loadAllWorkspacesOrderedByName();
    }

    @PostConstruct
    public void loadAllWorkspacesOrderedByName()
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
