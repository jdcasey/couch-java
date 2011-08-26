package org.commonjava.poc.mdb.db;

import java.util.ArrayList;
import java.util.List;

import org.apache.maven.mae.MAEException;
import org.apache.maven.mae.app.AbstractMAEApplication;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;
import org.commonjava.poc.mdb.session.SimpleDependencyDBSession;
import org.junit.Test;

public class DependencyDatabaseTest
{

    @Test
    public void validateConnection()
        throws Exception
    {
        DependencyDatabase db = new TestApplication().db;
        SimpleDependencyDBSession session =
            new SimpleDependencyDBSession( "http://developer.commonjava.org/db/maven-deps/" );

        db.validateConnection( session );
    }

    @Test
    public void storeThenDeleteDependencies()
        throws Exception
    {
        Model model = new Model();
        model.setGroupId( "org.foo" );
        model.setArtifactId( "test-store-deps" );
        model.setVersion( "1.0" );

        List<Dependency> deps = new ArrayList<Dependency>();
        Dependency dep = new Dependency();
        dep.setGroupId( "org.dep" );
        dep.setArtifactId( "dep-artifact" );
        dep.setVersion( "1.0.1" );

        deps.add( dep );

        model.setDependencies( deps );

        DependencyDatabase db = new TestApplication().db;

        SimpleDependencyDBSession session =
            new SimpleDependencyDBSession( "http://developer.commonjava.org/db/maven-deps/" );

        MavenProject project = new MavenProject( model );
        db.storeDependencies( project, session );
        db.deleteDependencies( project, session );
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
