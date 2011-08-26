package org.commonjava.maven.mdd.mapper;

import org.apache.maven.mae.project.session.ProjectToolsSession;
import org.commonjava.maven.mdd.db.session.DependencyDBSession;

public interface MapperSession
{
    ProjectToolsSession getProjectToolsSession();

    DependencyDBSession getDBSession();
}
