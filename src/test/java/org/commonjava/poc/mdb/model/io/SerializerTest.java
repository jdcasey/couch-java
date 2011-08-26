package org.commonjava.poc.mdb.model.io;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import org.apache.maven.mae.MAEException;
import org.apache.maven.mae.app.AbstractMAEApplication;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;
import org.commonjava.poc.mdb.model.Artifact;
import org.commonjava.poc.mdb.model.DependencyRelationship;
import org.commonjava.poc.mdb.model.DependencyRelationshipListing;
import org.junit.Test;

public class SerializerTest
{

    @Test
    public void dependencyToString()
        throws MAEException
    {
        DependencyRelationship dep =
            new DependencyRelationship( new Artifact( "org.apache.maven", "maven-core", "3.0.3" ),
                                        new Artifact( "org.commonjava.poc", "maven-dependency-db",
                                                      "1.0-SNAPSHOT" ), "jar", "compile" );

        String result = new TestApplication().serializer.toString( dep );

        System.out.println( result );
    }

    @Test
    public void dependencyRoundTrip()
        throws MAEException
    {
        DependencyRelationship dep =
            new DependencyRelationship( new Artifact( "org.apache.maven", "maven-core", "3.0.3" ),
                                        new Artifact( "org.commonjava.poc", "maven-dependency-db",
                                                      "1.0-SNAPSHOT" ), "jar", "compile" );

        Serializer serializer = new TestApplication().serializer;
        String result = serializer.toString( dep );
        DependencyRelationship out = serializer.toDependency( result );

        assertThat( out.getDependency(), equalTo( dep.getDependency() ) );
        assertThat( out.getDependent(), equalTo( dep.getDependent() ) );
    }

    @Test
    public void dependencyListToString()
        throws MAEException
    {
        DependencyRelationship dep =
            new DependencyRelationship( new Artifact( "org.apache.maven", "maven-core", "3.0.3" ),
                                        new Artifact( "org.commonjava.poc", "maven-dependency-db",
                                                      "1.0-SNAPSHOT" ), "jar", "compile" );

        String result =
            new TestApplication().serializer.toString( new DependencyRelationshipListing( dep ) );
        System.out.println( result );
    }

    @Test
    public void dependencyListRoundTrip()
        throws MAEException
    {
        DependencyRelationship dep =
            new DependencyRelationship( new Artifact( "org.apache.maven", "maven-core", "3.0.3" ),
                                        new Artifact( "org.commonjava.poc", "maven-dependency-db",
                                                      "1.0-SNAPSHOT" ), "jar", "compile" );

        DependencyRelationshipListing in = new DependencyRelationshipListing( dep );

        Serializer serializer = new TestApplication().serializer;
        String result = serializer.toString( in );

        DependencyRelationshipListing out = serializer.toDependencyListing( result );

        assertThat( out.size(), equalTo( 1 ) );

        DependencyRelationship outDep = out.iterator().next();
        assertThat( outDep.getDependency(), equalTo( dep.getDependency() ) );
        assertThat( outDep.getDependent(), equalTo( dep.getDependent() ) );
    }

    @Component( role = TestApplication.class )
    private static final class TestApplication
        extends AbstractMAEApplication
    {
        @Requirement
        private Serializer serializer;

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
