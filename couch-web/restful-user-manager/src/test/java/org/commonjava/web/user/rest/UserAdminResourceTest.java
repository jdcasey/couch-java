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

import javax.inject.Inject;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.commonjava.auth.couch.data.PasswordManager;
import org.commonjava.auth.couch.model.Role;
import org.commonjava.auth.couch.model.User;
import org.commonjava.web.common.model.Listing;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.gson.reflect.TypeToken;

@RunWith( Arquillian.class )
public class UserAdminResourceTest
    extends AbstractRESTfulUserManagerTest
{

    private static final String BASE_URL = "http://localhost:8080/test/admin/user";

    @Inject
    private PasswordManager passwordManager;

    @Test
    public void getAdminUser()
        throws Exception
    {
        User user = get( BASE_URL + "/" + User.ADMIN, User.class );

        assertThat( user, notNullValue() );
        assertThat( user.getUsername(), equalTo( User.ADMIN ) );
        assertThat( user.getFirstName(), equalTo( "Admin" ) );
        assertThat( user.getLastName(), equalTo( "User" ) );
        assertThat( user.getEmail(), equalTo( "admin@nowhere.com" ) );

        assertThat( user.getPasswordDigest(),
                    equalTo( passwordManager.digestPassword( "password" ) ) );

        assertThat( user.getRoles(), notNullValue() );
        assertThat( user.getRoles().size(), equalTo( 1 ) );
        assertThat( user.getRoles().iterator().next(), equalTo( Role.ADMIN ) );
    }

    @Test
    public void deleteAdminUser()
        throws Exception
    {
        delete( BASE_URL + "/" + User.ADMIN );
    }

    @Test
    public void createUser()
        throws Exception
    {
        User user = new User( "test", "test@nowhere.com", "Test", "User", "testPassword" );

        HttpResponse response = post( BASE_URL, user, HttpStatus.SC_CREATED );
        assertLocationHeader( response, BASE_URL + "/test" );
    }

    @Test
    public void modifyAdminUser()
        throws Exception
    {
        User user = get( BASE_URL + "/" + User.ADMIN, User.class );

        assertThat( user, notNullValue() );

        user.removeRole( Role.ADMIN );

        post( BASE_URL + "/" + User.ADMIN, user, HttpStatus.SC_OK );
    }

    @Test
    public void createUserThenRetrieveNewAndAdminUsers()
        throws Exception
    {
        User user = new User( "test", "test@nowhere.com", "Test", "User", "testPassword" );
        post( BASE_URL, user, HttpStatus.SC_CREATED );

        Listing<User> users = getListing( BASE_URL + "/list", new TypeToken<Listing<User>>()
        {} );

        assertThat( users, notNullValue() );
        assertThat( users.getItems(), notNullValue() );

        User u = users.getItems().get( 0 );
        assertThat( u.getUsername(), equalTo( "admin" ) );

        u = users.getItems().get( 1 );
        assertThat( u.getUsername(), equalTo( "test" ) );
    }

}
