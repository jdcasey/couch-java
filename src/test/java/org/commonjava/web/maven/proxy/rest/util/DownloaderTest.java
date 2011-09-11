package org.commonjava.web.maven.proxy.rest.util;

import static org.apache.commons.io.FileUtils.forceDelete;
import static org.apache.commons.io.FileUtils.readFileToString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.commonjava.web.maven.proxy.conf.DefaultProxyConfiguration;
import org.commonjava.web.maven.proxy.model.Repository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class DownloaderTest
{

    private Downloader downloader;

    private DefaultProxyConfiguration config;

    private File repoRoot;

    @Before
    public void setupTest()
        throws IOException
    {
        repoRoot = File.createTempFile( "repo.root.", ".dir" );
        repoRoot.delete();
        repoRoot.mkdirs();

        config = new DefaultProxyConfiguration();
        config.setDatabaseUrl( null );
        config.setRepositoryRootDirectory( repoRoot );

        downloader = new Downloader( config );
    }

    @After
    public void teardownTest()
        throws IOException
    {
        forceDelete( repoRoot );
    }

    @Test
    public void downloadOnePOMFromSingleRepository()
        throws IOException
    {
        Repository repo = new Repository( "central", "http://repo1.maven.apache.org/maven2/" );
        String path = "/org/apache/maven/maven-model/3.0.3/maven-model-3.0.3.pom";

        File downloaded = downloader.download( repo, path );
        String pom = readFileToString( downloaded );

        assertThat( pom.contains( "<artifactId>maven-model</artifactId>" ), equalTo( true ) );
    }

    @Test
    public void downloadOnePOMFromSecondRepositoryAfterDummyRepoFails()
        throws IOException
    {
        Repository repo = new Repository( "dummy", "http://www.nowhere.com/" );
        Repository repo2 = new Repository( "central", "http://repo1.maven.apache.org/maven2/" );

        String path = "/org/apache/maven/maven-model/3.0.3/maven-model-3.0.3.pom";

        List<Repository> repos = new ArrayList<Repository>();
        repos.add( repo );
        repos.add( repo2 );

        File downloaded = downloader.downloadFirst( repos, path );
        String pom = readFileToString( downloaded );

        assertThat( pom.contains( "<artifactId>maven-model</artifactId>" ), equalTo( true ) );
    }

}
