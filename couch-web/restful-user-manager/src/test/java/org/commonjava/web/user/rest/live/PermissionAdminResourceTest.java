/*******************************************************************************
 * Copyright (C) 2011  John Casey
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public
 * License along with this program.  If not, see 
 * <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package org.commonjava.web.user.rest.live;

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
import org.commonjava.web.test.fixture.TestWarArchiveBuilder;
import org.commonjava.web.user.rest.PermissionAdminResource;
import org.commonjava.web.user.rest.fixture.TestRESTApplication;
import org.commonjava.web.user.rest.fixture.TestUserManagerConfigProducer;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.gson.reflect.TypeToken;

@RunWith( Arquillian.class )
public class PermissionAdminResourceTest
    extends AbstractRESTfulUserManagerTest
{

    private static final String BASE_URL = "http://" + HOST + ":" + PORT + "/test/admin/permission";

    @Deployment
    public static WebArchive createWar()
    {
        return new TestWarArchiveBuilder( PermissionAdminResource.class ).withExtraClasses( AbstractRESTfulUserManagerTest.class,
                                                                                            TestRESTApplication.class,
                                                                                            TestUserManagerConfigProducer.class )
                                                                         .build();
    }

    @Test
    public void getGodPermission()
        throws Exception
    {
        final Permission perm = get( BASE_URL + "/*", Permission.class );

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
        final HttpResponse response = post( BASE_URL, new Permission( "test", Permission.READ ), HttpStatus.SC_CREATED );

        assertLocationHeader( response, BASE_URL + "/test:read" );
    }

    @Test
    public void createPermissionThenReadGodAndNewPermissions()
        throws Exception
    {
        post( BASE_URL, new Permission( "test", Permission.READ ), HttpStatus.SC_CREATED );

        final Listing<Permission> listing = getListing( BASE_URL + "/list", new TypeToken<Listing<Permission>>()
        {
        } );

        assertThat( listing, notNullValue() );

        final List<Permission> items = listing.getItems();
        Collections.sort( items, new Comparator<Permission>()
        {
            @Override
            public int compare( final Permission p1, final Permission p2 )
            {
                return p1.getName()
                         .compareTo( p2.getName() );
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
