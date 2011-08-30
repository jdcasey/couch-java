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

import static org.commonjava.maven.mdd.fixture.DBFixture.URL;
import static org.commonjava.maven.mdd.fixture.LoggingFixture.setupLogging;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.maven.mae.MAEException;
import org.apache.maven.mae.app.AbstractMAEApplication;
import org.apache.maven.mae.project.key.FullProjectKey;
import org.apache.maven.model.Dependency;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;
import org.commonjava.maven.mdd.db.session.SimpleDependencyDBSession;
import org.commonjava.maven.mdd.model.DependencyRelationship;
import org.commonjava.maven.mdd.model.DependencyRelationshipListing;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class DependencyDatabaseTest
{

    private TestApplication app;

    private final SimpleDependencyDBSession session = new SimpleDependencyDBSession( URL );

    @Before
    public void setupDB()
        throws Exception
    {
        app = new TestApplication();
        app.db.dropDatabase( session );
        app.db.installDatabase( session );
    }

    @After
    public void teardownDB()
        throws Exception
    {
        app.db.dropDatabase( session );
    }

    @Test
    public void validateConnection()
        throws Exception
    {
        DependencyDatabase db = app.db;

        db.validateConnection( session );
    }

    @Test
    public void storeAndReadDependencies()
        throws Exception
    {
        List<Dependency> deps = new ArrayList<Dependency>();
        Dependency dep = new Dependency();
        dep.setGroupId( "org.dep" );
        dep.setArtifactId( "dep-artifact" );
        dep.setVersion( "1.0.1" );

        deps.add( dep );

        DependencyDatabase db = app.db;

        FullProjectKey key = new FullProjectKey( "org.foo", "test-store-deps", "1.0" );

        db.storeDependencies( key, deps, session );
        DependencyRelationshipListing listing = db.getDirectDependencies( key, session );

        assertThat( listing, notNullValue() );
        assertThat( listing.size(), equalTo( 1 ) );

        DependencyRelationship rel = listing.iterator().next();

        assertThat( rel.getDependency().getGroupId(), equalTo( dep.getGroupId() ) );
        assertThat( rel.getDependency().getArtifactId(), equalTo( dep.getArtifactId() ) );
        assertThat( rel.getDependency().getVersion(), equalTo( dep.getVersion() ) );
    }

    @Test
    public void storeAndReadDependents()
        throws Exception
    {
        List<Dependency> deps = new ArrayList<Dependency>();
        Dependency dep = new Dependency();
        dep.setGroupId( "org.dep" );
        dep.setArtifactId( "dep-artifact" );
        dep.setVersion( "1.0.1" );

        deps.add( dep );

        DependencyDatabase db = app.db;

        FullProjectKey pk = new FullProjectKey( "org.foo", "test-store-deps", "1.0" );
        FullProjectKey dk = new FullProjectKey( dep );

        db.storeDependencies( pk, deps, session );
        DependencyRelationshipListing listing = db.getDirectDependents( dk, session );

        assertThat( listing.size(), equalTo( 1 ) );

        DependencyRelationship rel = listing.iterator().next();

        assertThat( rel.getDependent().getGroupId(), equalTo( pk.getGroupId() ) );
        assertThat( rel.getDependent().getArtifactId(), equalTo( pk.getArtifactId() ) );
        assertThat( rel.getDependent().getVersion(), equalTo( pk.getVersion() ) );

        System.out.println( listing );
    }

    @Test
    public void storeThenDeleteDependencies()
        throws Exception
    {
        List<Dependency> deps = new ArrayList<Dependency>();
        Dependency dep = new Dependency();
        dep.setGroupId( "org.dep" );
        dep.setArtifactId( "dep-artifact" );
        dep.setVersion( "1.0.1" );

        deps.add( dep );

        DependencyDatabase db = app.db;

        FullProjectKey key = new FullProjectKey( "org.foo", "test-store-deps", "1.0" );

        db.storeDependencies( key, deps, session );
        db.deleteDependencies( key, deps, session );
    }

    @BeforeClass
    public static void initLogging()
    {
        setupLogging( Level.DEBUG );
    }

    @Component( role = TestApplication.class )
    private static final class TestApplication
        extends AbstractMAEApplication
    {
        @Requirement
        private DependencyDatabase db;

        TestApplication()
            throws MAEException
        {
            load();
        }

        @Override
        public String getId()
        {
            return "test";
        }

        @Override
        public String getName()
        {
            return "Test Application";
        }
    }

}
