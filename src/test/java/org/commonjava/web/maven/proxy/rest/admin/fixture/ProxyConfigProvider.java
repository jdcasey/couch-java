package org.commonjava.web.maven.proxy.rest.admin.fixture;

import java.io.File;
import java.util.Properties;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;

import org.commonjava.web.maven.proxy.conf.DefaultProxyConfiguration;
import org.commonjava.web.maven.proxy.conf.ProxyConfiguration;
import org.commonjava.web.test.fixture.TestPropertyDefinitions;

public class ProxyConfigProvider
{

    public static final String REPO_ROOT_DIR = "repo.root.dir";

    @Inject
    @Named( TestPropertyDefinitions.NAMED )
    private Properties testProperties;

    @Produces
    public ProxyConfiguration getProxyConfiguration()
    {
        DefaultProxyConfiguration config = new DefaultProxyConfiguration();

        config.setDatabaseUrl( testProperties.getProperty( TestPropertyDefinitions.DATABASE_URL ) );
        config.setRepositoryRootDirectory( new File( System.getProperty( REPO_ROOT_DIR ),
                                                     "target/repo-downloads" ) );

        return config;
    }

}
