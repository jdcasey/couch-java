/*******************************************************************************
 * Copyright (C) 2011  John Casey
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package org.commonjava.maven.mdd.db;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.maven.mae.project.key.FullProjectKey;
import org.apache.maven.model.Dependency;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;
import org.commonjava.couch.db.CouchDBException;
import org.commonjava.couch.db.CouchManager;
import org.commonjava.couch.db.model.ViewRequest;
import org.commonjava.couch.model.CouchApp;
import org.commonjava.couch.model.io.CouchAppReader;
import org.commonjava.maven.mdd.db.session.DependencyDBSession;
import org.commonjava.maven.mdd.model.DependencyRelationship;

@Component( role = DependencyDatabase.class )
public class DependencyDatabase
{

    private static final String KEY = "key";

    @Requirement
    private CouchManager couch;

    protected synchronized CouchManager getCouch()
    {
        return couch;
    }

    public void dropDatabase( final DependencyDBSession session )
        throws DatabaseException
    {
        try
        {
            getCouch().dropDatabase( session.getBaseUrl() );
        }
        catch ( CouchDBException e )
        {
            throw new DatabaseException( "Failed to drop dependency database. Reason: %s", e,
                                         e.getMessage() );
        }
    }

    public void installDatabase( final DependencyDBSession session )
        throws DatabaseException
    {
        try
        {
            if ( !getCouch().dbExists( session.getBaseUrl() ) )
            {
                getCouch().createDatabase( session.getBaseUrl() );
            }
        }
        catch ( CouchDBException e )
        {
            throw new DatabaseException( "Failed to create dependency database. Reason: %s", e,
                                         e.getMessage() );
        }

        String app = session.getLogicApplication();
        try
        {
            if ( !getCouch().appExists( session.getBaseUrl(), app ) )
            {
                CouchApp application =
                    new CouchAppReader().readAppDefinition( DependencyDBSession.LOGIC_APPLICATION_RESOURCE_BASE );
                getCouch().installApplication( application, session.getBaseUrl() );
            }
        }
        catch ( CouchDBException e )
        {
            throw new DatabaseException( "Failed to install application: %s Reason: %s", e, app,
                                         e.getMessage() );
        }
        catch ( IOException e )
        {
            throw new DatabaseException( "Failed to install application: %s Reason: %s", e, app,
                                         e.getMessage() );
        }

    }

    public boolean validateConnection( final DependencyDBSession session )
        throws DatabaseException
    {
        try
        {
            String url = session.getBaseUrl();
            String[] views =
                { MDDViewRequest.DIRECT_DEPENDENCIES, MDDViewRequest.DIRECT_DEPENDENTS };

            if ( !getCouch().exists( url ) )
            {
                return false;
            }

            for ( String view : views )
            {
                if ( !getCouch().viewExists( url, session.getLogicApplication(), view ) )
                {
                    return false;
                }
            }
        }
        catch ( CouchDBException e )
        {
            throw new DatabaseException(
                                         "Failed to validate connection to dependency db: %s.\nReason: %s",
                                         e, session.getBaseUrl(), e.getMessage() );
        }

        return true;
    }

    public List<DependencyRelationship> getDirectDependencies( final FullProjectKey projectKey,
                                                               final DependencyDBSession session )
        throws DatabaseException
    {
        ViewRequest req = new MDDViewRequest( MDDViewRequest.DIRECT_DEPENDENCIES, session );
        req.setParameter( KEY, projectKey );
        try
        {
            return getCouch().getViewListing( req, session.getBaseUrl(),
                                              DependencyRelationship.class );
        }
        catch ( CouchDBException e )
        {
            throw new DatabaseException(
                                         "Failed to retrieve direct dependencies for: %s. Reason: %s",
                                         e, projectKey, e.getMessage() );
        }
    }

    public List<DependencyRelationship> getDirectDependents( final FullProjectKey projectKey,
                                                             final DependencyDBSession session )
        throws DatabaseException
    {
        ViewRequest req = new MDDViewRequest( MDDViewRequest.DIRECT_DEPENDENTS, session );
        req.setParameter( KEY, projectKey );
        try
        {
            return getCouch().getViewListing( req, session.getBaseUrl(),
                                              DependencyRelationship.class );
        }
        catch ( CouchDBException e )
        {
            throw new DatabaseException(
                                         "Failed to retrieve direct dependencies for: %s. Reason: %s",
                                         e, projectKey, e.getMessage() );
        }
    }

    public void store( final Collection<DependencyRelationship> rels,
                       final DependencyDBSession session )
        throws DatabaseException
    {
        try
        {
            getCouch().store( rels, session.getBaseUrl(), true, false );
        }
        catch ( CouchDBException e )
        {
            throw new DatabaseException( "Failed to store %d dependency relationships: %s", e,
                                         rels.size(), e.getMessage() );
        }
    }

    public List<DependencyRelationship> storeDependencies( final FullProjectKey projectKey,
                                                           final List<Dependency> dependencies,
                                                           final DependencyDBSession session )
        throws DatabaseException
    {
        Set<DependencyRelationship> rels = new LinkedHashSet<DependencyRelationship>();
        int count = 0;
        for ( Dependency dep : dependencies )
        {
            rels.add( new DependencyRelationship( dep, projectKey, count ) );
            count++;
        }

        store( rels, session );

        return new ArrayList<DependencyRelationship>( rels );
    }

    public void store( final DependencyRelationship rel, final DependencyDBSession session )
        throws DatabaseException
    {
        try
        {
            getCouch().store( rel, session.getBaseUrl(), true );
        }
        catch ( CouchDBException e )
        {
            throw new DatabaseException( "Failed to store dependency relationship: . Reason: %s",
                                         e, rel, e.getMessage() );
        }
    }

    public void deleteDependencies( final FullProjectKey projectKey,
                                    final List<Dependency> dependencies,
                                    final DependencyDBSession session )
        throws DatabaseException
    {
        Set<DependencyRelationship> rels = new HashSet<DependencyRelationship>();
        for ( Dependency dep : dependencies )
        {
            rels.add( new DependencyRelationship( dep, projectKey ) );
        }

        try
        {
            getCouch().delete( rels, session.getBaseUrl(), false );
        }
        catch ( CouchDBException e )
        {
            throw new DatabaseException( "Failed to delete %d dependency relationships: %s", e,
                                         rels.size(), e.getMessage() );
        }
    }

    public void delete( final DependencyRelationship rel, final DependencyDBSession session )
        throws DatabaseException
    {
        try
        {
            getCouch().delete( rel, session.getBaseUrl() );
        }
        catch ( CouchDBException e )
        {
            throw new DatabaseException(
                                         "Failed to delete dependency relationship: %s. Reason: %s",
                                         e, rel, e.getMessage() );
        }
    }

    public boolean hasDirectDependency( final FullProjectKey dependent,
                                        final FullProjectKey dependency,
                                        final DependencyDBSession session )
        throws DatabaseException
    {
        return hasDirectDependency( new DependencyRelationship( dependency, dependent ), session );
    }

    public boolean hasDirectDependency( final DependencyRelationship rel,
                                        final DependencyDBSession session )
        throws DatabaseException
    {
        try
        {
            return getCouch().exists( rel, session.getBaseUrl() );
        }
        catch ( CouchDBException e )
        {
            throw new DatabaseException(
                                         "Failed to check for existing dependency relationship: %s. Reason: %s",
                                         e, rel, e.getMessage() );
        }
    }

}
