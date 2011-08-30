/*******************************************************************************
 * Copyright (C) 2011  John Casey
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package org.commonjava.maven.mdd.model.io;

import static org.commonjava.maven.mdd.fixture.LoggingFixture.setupLogging;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.apache.log4j.Level;
import org.apache.maven.mae.MAEException;
import org.apache.maven.mae.app.AbstractMAEApplication;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;
import org.commonjava.couch.db.model.CouchObjectList;
import org.commonjava.couch.model.io.CouchObjectListDeserializer;
import org.commonjava.couch.model.io.Serializer;
import org.commonjava.maven.mdd.model.Artifact;
import org.commonjava.maven.mdd.model.DependencyRelationship;
import org.junit.BeforeClass;
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
                                                      "1.0-SNAPSHOT" ), "jar", "compile", 1 );

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
                                                      "1.0-SNAPSHOT" ), "jar", "compile", 1 );

        Serializer serializer = new TestApplication().serializer;
        String result = serializer.toString( dep );
        DependencyRelationship out = serializer.toDocument( result, DependencyRelationship.class );

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

        CouchObjectListDeserializer<DependencyRelationship> deser =
            new CouchObjectListDeserializer<DependencyRelationship>( DependencyRelationship.class );

        CouchObjectList<DependencyRelationship> listing =
            serializer.fromJson( result, deser.typeLiteral(), deser );

        assertThat( listing, notNullValue() );

        List<DependencyRelationship> out = listing.getItems();

        assertThat( out.size(), equalTo( 1 ) );

        DependencyRelationship outDep = out.get( 0 );

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
