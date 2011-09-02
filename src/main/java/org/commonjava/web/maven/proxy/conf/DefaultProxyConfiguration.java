package org.commonjava.web.maven.proxy.conf;

import java.io.File;

import org.commonjava.web.config.annotation.ConfigName;
import org.commonjava.web.config.annotation.SectionName;
import org.commonjava.web.config.section.ConfigurationSectionListener;

@SectionName( ConfigurationSectionListener.DEFAULT_SECTION )
public class DefaultProxyConfiguration
    implements ProxyConfiguration
{

    protected static final File DEFAULT_REPO_ROOT_DIR =
        new File( "/var/lib/artifact-proxy/repositories" );

    private File repositoryRootDirectory = DEFAULT_REPO_ROOT_DIR;

    private String logicApplication;

    private String databaseUrl;

    @Override
    public String getDatabaseUrl()
    {
        return databaseUrl;
    }

    @Override
    public String getLogicApplication()
    {
        return logicApplication;
    }

    @Override
    public File getRepositoryRootDirectory()
    {
        return repositoryRootDirectory;
    }

    @ConfigName( "repositoy.root.dir" )
    public void setRepositoryRootDirectory( final File repositoryRootDirectory )
    {
        this.repositoryRootDirectory = repositoryRootDirectory;
    }

    @ConfigName( "db.application" )
    public void setLogicApplication( final String logicApplication )
    {
        this.logicApplication = logicApplication;
    }

    @ConfigName( "database.url" )
    public void setDatabaseUrl( final String databaseUrl )
    {
        this.databaseUrl = databaseUrl;
    }

}
