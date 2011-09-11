package org.commonjava.web.maven.proxy.rest.admin;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.commonjava.web.common.model.Listing;
import org.commonjava.web.maven.proxy.model.Repository;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.gson.reflect.TypeToken;

@RunWith( Arquillian.class )
public class RepositoryAdminResourceTest
    extends AbstractAProxRESTTest
{

    private static final String BASE_URL = "http://localhost:8080/test/api/1.0/admin/repository";

    @Test
    public void createAndRetrieveCentralRepoProxy()
        throws Exception
    {
        Repository repo = new Repository( "central", "http://repo1.maven.apache.org/maven2/" );
        HttpResponse response = post( BASE_URL, repo, HttpStatus.SC_CREATED );

        String repoUrl = BASE_URL + "/" + repo.getName();
        assertLocationHeader( response, repoUrl );

        Repository result = get( repoUrl, Repository.class );

        assertThat( result.getName(), equalTo( repo.getName() ) );
        assertThat( result.getUrl(), equalTo( repo.getUrl() ) );
        assertThat( result.getUser(), nullValue() );
        assertThat( result.getPassword(), nullValue() );
    }

    @Test
    public void createCentralRepoProxyTwiceAndRetrieveOne()
        throws Exception
    {
        Repository repo = new Repository( "central", "http://repo1.maven.apache.org/maven2/" );
        post( BASE_URL, repo, HttpStatus.SC_CREATED );

        post( BASE_URL, repo, HttpStatus.SC_CONFLICT );

        Listing<Repository> result =
            getListing( BASE_URL + "/list", new TypeToken<Listing<Repository>>()
            {} );

        assertThat( result, notNullValue() );

        List<Repository> items = result.getItems();

        assertThat( items, notNullValue() );
        assertThat( items.size(), equalTo( 1 ) );

        Repository r = items.get( 0 );
        assertThat( r.getName(), equalTo( repo.getName() ) );
        assertThat( r.getUrl(), equalTo( repo.getUrl() ) );
    }

    @Test
    public void createAndDeleteCentralRepoProxy_ByName()
        throws Exception
    {
        Repository repo = new Repository( "central", "http://repo1.maven.apache.org/maven2/" );
        post( BASE_URL, repo, HttpStatus.SC_CREATED );

        delete( BASE_URL + "/" + repo.getName() );

        get( BASE_URL + "/" + repo.getName(), HttpStatus.SC_NOT_FOUND );
    }

    @Test
    public void createTwoReposAndRetrieveAll()
        throws Exception
    {
        Repository repo = new Repository( "central", "http://repo1.maven.apache.org/maven2/" );
        post( BASE_URL, repo, HttpStatus.SC_CREATED );

        Repository repo2 = new Repository( "test", "http://www.google.com" );
        post( BASE_URL, repo2, HttpStatus.SC_CREATED );

        Listing<Repository> result =
            getListing( BASE_URL + "/list", new TypeToken<Listing<Repository>>()
            {} );

        assertThat( result, notNullValue() );

        List<Repository> repositories = result.getItems();

        assertThat( repositories, notNullValue() );
        assertThat( repositories.size(), equalTo( 2 ) );

        Collections.sort( repositories, new Comparator<Repository>()
        {

            @Override
            public int compare( final Repository r1, final Repository r2 )
            {
                return r1.getName().compareTo( r2.getName() );
            }
        } );

        Repository r = repositories.get( 0 );
        assertThat( r.getName(), equalTo( repo.getName() ) );

        r = repositories.get( 1 );
        assertThat( r.getName(), equalTo( repo2.getName() ) );
    }

}
