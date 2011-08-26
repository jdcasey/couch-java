package org.commonjava.maven.mdd.db;

import static org.commonjava.maven.mdd.fixture.DBFixture.URL;
import static org.commonjava.maven.mdd.fixture.LoggingFixture.setupLogging;
import static org.hamcrest.CoreMatchers.equalTo;
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
import org.junit.BeforeClass;
import org.junit.Test;

public class DependencyDatabaseTest
{

    @Test
    public void validateConnection()
        throws Exception
    {
        DependencyDatabase db = new TestApplication().db;
        SimpleDependencyDBSession session = new SimpleDependencyDBSession( URL );

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

        DependencyDatabase db = new TestApplication().db;

        SimpleDependencyDBSession session = new SimpleDependencyDBSession( URL );

        FullProjectKey key = new FullProjectKey( "org.foo", "test-store-deps", "1.0" );

        db.storeDependencies( key, deps, session );
        DependencyRelationshipListing listing = db.getDirectDependencies( key, session );

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

        DependencyDatabase db = new TestApplication().db;

        SimpleDependencyDBSession session = new SimpleDependencyDBSession( URL );

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

        DependencyDatabase db = new TestApplication().db;

        SimpleDependencyDBSession session = new SimpleDependencyDBSession( URL );

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
