package org.commonjava.web.maven.proxy.conf;

import java.io.File;

import javax.enterprise.inject.Alternative;
import javax.inject.Named;

import org.commonjava.web.config.annotation.ConfigName;
import org.commonjava.web.config.annotation.SectionName;
import org.commonjava.web.config.section.ConfigurationSectionListener;
import org.commonjava.web.maven.proxy.data.ProxyViewRequest;

@SectionName( ConfigurationSectionListener.DEFAULT_SECTION )
@Alternative
@Named( "unused" )
public class DefaultProxyConfiguration
    implements ProxyConfiguration
{

    protected static final File DEFAULT_REPO_ROOT_DIR =
        new File( "/var/lib/artifact-proxy/repositories" );

    private File repositoryRootDirectory = DEFAULT_REPO_ROOT_DIR;

    private String logicApplication = ProxyViewRequest.APPLICATION_RESOURCE;

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

    @ConfigName( "db.url" )
    public void setDatabaseUrl( final String databaseUrl )
    {
        this.databaseUrl = databaseUrl;
    }

}
