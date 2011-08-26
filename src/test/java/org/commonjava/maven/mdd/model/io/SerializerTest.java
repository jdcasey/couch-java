package org.commonjava.maven.mdd.model.io;

import static org.commonjava.maven.mdd.fixture.LoggingFixture.setupLogging;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.io.ByteArrayInputStream;

import org.apache.log4j.Level;
import org.apache.maven.mae.MAEException;
import org.apache.maven.mae.app.AbstractMAEApplication;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;
import org.commonjava.maven.mdd.model.Artifact;
import org.commonjava.maven.mdd.model.DatabaseError;
import org.commonjava.maven.mdd.model.DependencyRelationship;
import org.commonjava.maven.mdd.model.DependencyRelationshipListing;
import org.commonjava.maven.mdd.model.io.Serializer;
import org.junit.BeforeClass;
import org.junit.Test;

public class SerializerTest
{

    @Test
    public void errorToString()
        throws Exception
    {
        String json = "{\"error\":\"bad_request\",\"reason\":\"invalid UTF-8 JSON\"}";
        DatabaseError error =
            new TestApplication().serializer.toError( new ByteArrayInputStream(
                                                                                json.getBytes( "UTF-8" ) ),
                                                      "UTF-8" );
        System.out.println( error );
    }

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
    public void dependencyListRoundTrip()
        throws MAEException
    {
        Serializer serializer = new TestApplication().serializer;
        String result =
            "{\"total_rows\":1,\"offset\":0,\"rows\":[\n"
                + "{\"id\":\"org.foo:test-store-deps:1.0_org.dep:dep-artifact:1.0.1\","
                + "\"key\":\"org.foo:test-store-deps:1.0\","
                + "\"value\":\"org.dep:dep-artifact:1.0.1\","
                + "\"doc\":{\"_id\":\"org.foo:test-store-deps:1.0_org.dep:dep-artifact:1.0.1\","
                + "         \"_rev\":\"3-cdcdb08d4b7ee87985324b5495253af5\","
                + "         \"dependency\":{\"groupId\":\"org.dep\","
                + "                         \"artifactId\":\"dep-artifact\","
                + "                         \"version\":\"1.0.1\"},"
                + "         \"dependent\":{\"groupId\":\"org.foo\","
                + "                        \"artifactId\":\"test-dependent\","
                + "                         \"version\":\"1.0\"}," + "         \"type\":\"jar\","
                + "         \"scope\":\"compile\"}}\n" + "]}";

        DependencyRelationshipListing out = serializer.toDependencyListing( result );

        assertThat( out.size(), equalTo( 1 ) );

        DependencyRelationship outDep = out.iterator().next();

        assertThat( outDep.getDependency().getGroupId(), equalTo( "org.dep" ) );
        assertThat( outDep.getDependency().getArtifactId(), equalTo( "dep-artifact" ) );
        assertThat( outDep.getDependency().getVersion(), equalTo( "1.0.1" ) );

        assertThat( outDep.getDependent().getGroupId(), equalTo( "org.foo" ) );
        assertThat( outDep.getDependent().getArtifactId(), equalTo( "test-dependent" ) );
        assertThat( outDep.getDependent().getVersion(), equalTo( "1.0" ) );

        assertThat( outDep.getType(), equalTo( "jar" ) );
        assertThat( outDep.getScope(), equalTo( "compile" ) );
    }

    @BeforeClass
    public static void initLogging()
    {
        setupLogging( Level.INFO );
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
