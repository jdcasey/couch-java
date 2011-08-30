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
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.maven.mae.project.key.FullProjectKey;
import org.apache.maven.model.Dependency;
import org.codehaus.plexus.component.annotations.Component;
import org.commonjava.couch.db.CouchDBException;
import org.commonjava.couch.db.CouchManager;
import org.commonjava.couch.db.model.ViewRequest;
import org.commonjava.couch.model.CouchApp;
import org.commonjava.couch.model.io.CouchAppReader;
import org.commonjava.maven.mdd.db.session.DependencyDBSession;
import org.commonjava.maven.mdd.model.DependencyRelationship;
import org.commonjava.maven.mdd.model.DependencyRelationshipListing;
import org.commonjava.maven.mdd.model.io.MDDSerializer;

@Component( role = DependencyDatabase.class )
public class DependencyDatabase
{

    private static final String KEY = "key";

    private static final String INCLUDE_DOCS = "include_docs";

    private CouchManager couch;

    protected synchronized CouchManager getCouch()
    {
        if ( couch == null )
        {
            couch = new CouchManager( new MDDSerializer() );
        }

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
            if ( !getCouch().exists( session.getBaseUrl() ) )
            {
                getCouch().createDatabase( session.getBaseUrl() );
            }
        }
        catch ( CouchDBException e )
        {
            throw new DatabaseException( "Failed to create dependency database. Reason: %s", e,
                                         e.getMessage() );
        }

        try
        {
            if ( !getCouch().appExists( session.getBaseUrl(), MDDViewRequest.LOGIC_APP ) )
            {
                CouchApp app = new CouchAppReader().readAppDefinition( MDDViewRequest.LOGIC_APP );
                getCouch().installApplication( app, session.getBaseUrl() );
            }
        }
        catch ( CouchDBException e )
        {
            throw new DatabaseException( "Failed to install application: %s Reason: %s", e,
                                         MDDViewRequest.LOGIC_APP, e.getMessage() );
        }
        catch ( IOException e )
        {
            throw new DatabaseException( "Failed to install application: %s Reason: %s", e,
                                         MDDViewRequest.LOGIC_APP, e.getMessage() );
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
                if ( !getCouch().viewExists( url, MDDViewRequest.LOGIC_APP, view ) )
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

    public DependencyRelationshipListing getDirectDependencies( final FullProjectKey projectKey,
                                                                final DependencyDBSession session )
        throws DatabaseException
    {
        return getDependencyRelationshipView( "direct-dependencies", projectKey, session );
    }

    public DependencyRelationshipListing getDirectDependents( final FullProjectKey projectKey,
                                                              final DependencyDBSession session )
        throws DatabaseException
    {
        return getDependencyRelationshipView( "direct-dependents", projectKey, session );
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

    public void storeDependencies( final FullProjectKey projectKey,
                                   final List<Dependency> dependencies,
                                   final DependencyDBSession session )
        throws DatabaseException
    {
        Set<DependencyRelationship> rels = new HashSet<DependencyRelationship>();
        for ( Dependency dep : dependencies )
        {
            rels.add( new DependencyRelationship( dep, projectKey ) );
        }

        store( rels, session );
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
        return hasDirectDependency( new DependencyRelationship( dependent, dependency ), session );
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

    protected DependencyRelationshipListing getDependencyRelationshipView( final String viewName,
                                                                           final FullProjectKey projectKey,
                                                                           final DependencyDBSession session )
        throws DatabaseException
    {
        ViewRequest req = new ViewRequest( session.getLogicApplication(), viewName );
        req.setParameter( KEY, projectKey );
        req.setParameter( INCLUDE_DOCS, true );

        try
        {
            return getCouch().getView( req, session.getBaseUrl(),
                                       DependencyRelationshipListing.class );
        }
        catch ( CouchDBException e )
        {
            throw new DatabaseException( "Failed to retrieve view: %s. Reason: %s", e, viewName,
                                         e.getMessage() );
        }
    }

}
