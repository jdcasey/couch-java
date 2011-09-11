package org.commonjava.web.maven.proxy.data;

import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.commonjava.web.maven.proxy.model.Group;
import org.commonjava.web.maven.proxy.model.Repository;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.Before;
import org.junit.Test;

public class GroupDataManagerTest
    extends AbstractProxyDataManagerTest
{

    @Before
    public void seedRepositoriesForGroupTests()
        throws ProxyDataException
    {
        manager.storeRepository( new Repository( "central", "http://repo1.maven.apache.org/maven2/" ) );
        manager.storeRepository( new Repository( "repo2", "http://repo1.maven.org/maven2/" ) );
    }

    @Test
    @SuppressWarnings( { "unchecked", "rawtypes" } )
    public void createAndRetrieveEmptyGroup()
        throws ProxyDataException
    {
        Group grp = new Group( "test" );

        manager.storeGroup( grp );

        Group result = manager.getGroup( grp.getName() );

        assertThat( result, notNullValue() );
        assertThat( result.getName(), equalTo( grp.getName() ) );
        assertThat( result.getConstituents(), anyOf( nullValue(), new BaseMatcher<List>()
        {
            @Override
            public boolean matches( final Object item )
            {
                return ( item instanceof List ) && ( (List) item ).isEmpty();
            }

            @Override
            public void describeTo( final Description description )
            {
                description.appendText( "empty list" );
            }
        } ) );
    }

    @Test
    public void createAndDeleteGroup_ByName()
        throws ProxyDataException
    {
        Group grp = new Group( "test" );

        manager.storeGroup( grp );

        manager.deleteGroup( grp.getName() );

        Group result = manager.getGroup( grp.getName() );

        assertThat( result, nullValue() );
    }

    @Test
    public void createAndDeleteGroup_ByObject()
        throws ProxyDataException
    {
        Group grp = new Group( "test" );

        manager.storeGroup( grp );

        manager.deleteGroup( grp );

        Group result = manager.getGroup( grp.getName() );

        assertThat( result, nullValue() );
    }

    @Test
    public void createAndRetrieveGroupWithTwoConstituents()
        throws ProxyDataException
    {
        Group grp = new Group( "test", "central", "repo2" );

        manager.storeGroup( grp );

        Group result = manager.getGroup( grp.getName() );

        assertThat( result, notNullValue() );
        assertThat( result.getName(), equalTo( grp.getName() ) );

        List<String> repos = result.getConstituents();
        assertThat( repos, notNullValue() );
        assertThat( repos.size(), equalTo( 2 ) );

        assertThat( repos.get( 0 ), equalTo( "central" ) );
        assertThat( repos.get( 1 ), equalTo( "repo2" ) );
    }

    @Test
    public void createGroupAndRetrieveRepositoryConstituents()
        throws ProxyDataException
    {
        Group grp = new Group( "test", "central", "repo2" );

        manager.storeGroup( grp );

        List<Repository> result = manager.getRepositoriesForGroup( grp.getName() );

        assertThat( result, notNullValue() );
        assertThat( result.size(), equalTo( 2 ) );

        Repository repo = result.get( 0 );
        assertThat( repo, notNullValue() );
        assertThat( repo.getName(), equalTo( "central" ) );

        repo = result.get( 1 );
        assertThat( repo, notNullValue() );
        assertThat( repo.getName(), equalTo( "repo2" ) );
    }

    @Test
    public void createSameGroupTwiceAndRetrieveOne()
        throws ProxyDataException
    {
        Group grp = new Group( "test" );

        manager.storeGroup( grp, true );
        manager.storeGroup( grp, true );

        List<Group> result = manager.getAllGroups();

        assertThat( result, notNullValue() );
        assertThat( result.size(), equalTo( 1 ) );
    }

    @Test
    public void createTwoGroupsAndRetrieveBoth()
        throws ProxyDataException
    {
        Group grp = new Group( "test" );
        Group grp2 = new Group( "test2" );

        manager.storeGroup( grp );
        manager.storeGroup( grp2 );

        List<Group> result = manager.getAllGroups();

        assertThat( result, notNullValue() );
        assertThat( result.size(), equalTo( 2 ) );

        Collections.sort( result, new Comparator<Group>()
        {
            @Override
            public int compare( final Group g1, final Group g2 )
            {
                return g1.getName().compareTo( g2.getName() );
            }
        } );

        Group g = result.get( 0 );
        assertThat( g.getName(), equalTo( grp.getName() ) );

        g = result.get( 1 );
        assertThat( g.getName(), equalTo( grp2.getName() ) );
    }

}
