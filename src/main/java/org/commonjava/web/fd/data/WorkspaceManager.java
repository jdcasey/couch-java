package org.commonjava.web.fd.data;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;

import javax.annotation.PostConstruct;
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

public class WorkspaceManager
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
