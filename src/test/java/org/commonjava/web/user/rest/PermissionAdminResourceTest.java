package org.commonjava.web.user.rest;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.commonjava.auth.couch.model.Permission;
import org.commonjava.web.common.model.Listing;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.gson.reflect.TypeToken;

@RunWith( Arquillian.class )
public class PermissionAdminResourceTest
    extends AbstractRESTfulUserManagerTest
{

    private static final String BASE_URL = "http://" + HOST + ":" + PORT + "/test/admin/permission";

    @Test
    public void getGodPermission()
        throws Exception
    {
        Permission perm = get( BASE_URL + "/*", Permission.class );

        assertThat( perm, notNullValue() );
        assertThat( perm.getName(), equalTo( "*" ) );
    }

    @Test
    public void deleteGodPermission()
        throws Exception
    {
        delete( BASE_URL + "/*" );
    }

    @Test
    public void createPermission()
        throws Exception
    {
        HttpResponse response =
            post( BASE_URL, new Permission( "test", Permission.READ ), HttpStatus.SC_CREATED );

        assertLocationHeader( response, BASE_URL + "/test:read" );
    }

    @Test
    public void createPermissionThenReadGodAndNewPermissions()
        throws Exception
    {
        post( BASE_URL, new Permission( "test", Permission.READ ), HttpStatus.SC_CREATED );

        Listing<Permission> listing =
            getListing( BASE_URL + "/list", new TypeToken<Listing<Permission>>()
            {} );

        assertThat( listing, notNullValue() );

        List<Permission> items = listing.getItems();
        Collections.sort( items, new Comparator<Permission>()
        {
            @Override
            public int compare( final Permission p1, final Permission p2 )
            {
                return p1.getName().compareTo( p2.getName() );
            }
        } );

        assertThat( items, notNullValue() );
        assertThat( items.size(), equalTo( 5 ) );

        int i = 0;
        Permission perm = items.get( i++ );
        assertThat( perm.getName(), equalTo( "*" ) );

        perm = items.get( i++ );
        assertThat( perm.getName(), equalTo( "permission:admin" ) );

        perm = items.get( i++ );
        assertThat( perm.getName(), equalTo( "role:admin" ) );

        perm = items.get( i++ );
        assertThat( perm.getName(), equalTo( "test:read" ) );

        perm = items.get( i++ );
        assertThat( perm.getName(), equalTo( "user:admin" ) );
    }

}
