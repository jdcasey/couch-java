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
package org.commonjava.web.user.rest;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.commonjava.auth.couch.model.Permission;
import org.commonjava.auth.couch.model.Role;
import org.commonjava.web.common.model.Listing;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.gson.reflect.TypeToken;

@RunWith( Arquillian.class )
public class RoleAdminResourceTest
    extends AbstractRESTfulUserManagerTest
{

    private static final String BASE_URL = "http://localhost:8080/test/admin/role";

    @Test
    public void getAdminRole()
        throws Exception
    {
        Role role = get( BASE_URL + "/admin", Role.class );

        assertThat( role, notNullValue() );
        assertThat( role.getName(), equalTo( "admin" ) );
        assertThat( role.getPermissions(), notNullValue() );
        assertThat( role.getPermissions().size(), equalTo( 1 ) );
        assertThat( role.getPermissions().iterator().next(), equalTo( "*" ) );
    }

    @Test
    public void deleteAdminRole()
        throws Exception
    {
        delete( BASE_URL + "/admin" );
    }

    @Test
    public void createRole()
        throws Exception
    {
        Role r = new Role( "test", new Permission( Permission.WILDCARD ) );

        HttpResponse response = post( BASE_URL, r, HttpStatus.SC_CREATED );
        assertLocationHeader( response, BASE_URL + "/test" );
    }

    @Test
    public void modifyAdminRole()
        throws Exception
    {
        Role role = get( BASE_URL + "/admin", Role.class );

        assertThat( role, notNullValue() );

        role.removePermission( Permission.WILDCARD );
        post( BASE_URL + "/admin", role, HttpStatus.SC_OK );
    }

    @Test
    public void createRoleThenGetNewAndAdminRoles()
        throws Exception
    {
        HttpResponse response =
            post( BASE_URL, new Role( "test", new Permission( Permission.WILDCARD ) ),
                  HttpStatus.SC_CREATED );

        assertLocationHeader( response, BASE_URL + "/test" );

        Listing<Role> roles = getListing( BASE_URL + "/list", new TypeToken<Listing<Role>>()
        {} );

        assertThat( roles, notNullValue() );
        assertThat( roles.getItems(), notNullValue() );

        Role r = roles.getItems().get( 0 );
        assertThat( r.getName(), equalTo( "admin" ) );

        r = roles.getItems().get( 1 );
        assertThat( r.getName(), equalTo( "test" ) );

    }
}
