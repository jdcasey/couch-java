/*******************************************************************************
 * Copyright (C) 2011  John Casey
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package org.commonjava.maven.mdd.mapper;

import static org.commonjava.maven.mdd.fixture.DBFixture.URL;
import static org.commonjava.maven.mdd.fixture.LoggingFixture.setupLogging;

import java.util.List;

import org.apache.log4j.Level;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.mae.MAEException;
import org.apache.maven.mae.app.AbstractMAEApplication;
import org.apache.maven.mae.depgraph.impl.FlexibleScopeDependencySelector;
import org.apache.maven.mae.depgraph.impl.collect.BareBonesDependencyCollector;
import org.apache.maven.mae.internal.container.ComponentKey;
import org.apache.maven.mae.internal.container.ComponentSelector;
import org.apache.maven.mae.project.key.FullProjectKey;
import org.apache.maven.mae.project.session.SimpleProjectToolsSession;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;
import org.commonjava.maven.mdd.db.DependencyDatabase;
import org.commonjava.maven.mdd.db.session.SimpleDependencyDBSession;
import org.commonjava.maven.mdd.model.DependencyRelationship;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.sonatype.aether.impl.DependencyCollector;

public class ProjectMapperTest
{

    private ProjectMapper mapper;

    private DependencyDatabase db;

    private MapperSession session;

    @BeforeClass
    public static void initLogging()
    {
        setupLogging( Level.DEBUG );
    }

    @Before
    public void setupTest()
        throws Exception
    {
        TestApplication app = new TestApplication();

        mapper = app.mapper;
        db = app.db;

        SimpleProjectToolsSession pts = new SimpleProjectToolsSession();
        pts.setProcessPomPlugins( false );
        pts.setDependencySelector( new FlexibleScopeDependencySelector( Artifact.SCOPE_PROVIDED,
                                                                        Artifact.SCOPE_TEST ) );

        SimpleDependencyDBSession dbs = new SimpleDependencyDBSession( URL );

        session = new SimpleMapperSession( dbs, pts );

        db.installDatabase( dbs );

        db.validateConnection( dbs );
    }

    @After
    public void teardownTest()
        throws Exception
    {
        if ( db != null && session != null )
        {
            db.dropDatabase( session.getDBSession() );
        }
    }

    @Test
    public void storeMavenDependencyGraph()
        throws Exception
    {
        FullProjectKey key = new FullProjectKey( "org.apache.maven", "maven-embedder", "3.0.3" );

        List<DependencyRelationship> rels = mapper.mapProjectDependencyGraph( key, session );
        System.out.println( "Mapped " + rels.size() + " relationships." );
    }

    @Test
    public void storeMavenDirectDependencies()
        throws Exception
    {
        FullProjectKey key = new FullProjectKey( "org.apache.maven", "maven-embedder", "3.0.3" );

        List<DependencyRelationship> rels = mapper.mapProjectDirectDependencies( key, session );
        System.out.println( "Mapped " + rels.size() + " relationships." );
    }

    @Component( role = TestApplication.class )
    private static final class TestApplication
        extends AbstractMAEApplication
    {
        @Requirement
        public DependencyDatabase db;

        @Requirement
        private ProjectMapper mapper;

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
            return "Test App";
        }

        @Override
        public ComponentSelector getComponentSelector()
        {
            ComponentSelector sel = new ComponentSelector();

            sel.setSelection( new ComponentKey<DependencyCollector>( DependencyCollector.class ),
                              BareBonesDependencyCollector.HINT );

            return sel;
        }

    }

}
