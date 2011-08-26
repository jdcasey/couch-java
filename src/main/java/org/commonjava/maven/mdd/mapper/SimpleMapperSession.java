package org.commonjava.maven.mdd.mapper;

import org.apache.maven.mae.project.session.ProjectToolsSession;
import org.apache.maven.mae.project.session.SimpleProjectToolsSession;
import org.commonjava.maven.mdd.db.session.DependencyDBSession;
import org.commonjava.maven.mdd.db.session.SimpleDependencyDBSession;

public class SimpleMapperSession
    implements MapperSession
{

    private final ProjectToolsSession projectToolsSession;

    private final DependencyDBSession dbSession;

    public SimpleMapperSession( final DependencyDBSession dbSession,
                                final ProjectToolsSession projectToolsSession )
    {
        this.dbSession = dbSession;
        this.projectToolsSession = projectToolsSession;
    }

    public SimpleMapperSession( final String dbBaseUrl )
    {
        this.dbSession = new SimpleDependencyDBSession( dbBaseUrl );
        this.projectToolsSession = new SimpleProjectToolsSession();
    }

    @Override
    public ProjectToolsSession getProjectToolsSession()
    {
        return projectToolsSession;
    }

    @Override
    public DependencyDBSession getDBSession()
    {
        return dbSession;
    }

}
