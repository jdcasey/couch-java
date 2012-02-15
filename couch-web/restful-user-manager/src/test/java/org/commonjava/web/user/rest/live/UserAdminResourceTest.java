/*******************************************************************************
 * Copyright 2011 John Casey
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package org.commonjava.web.user.rest.live;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.io.File;

import javax.inject.Inject;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.commonjava.auth.couch.data.PasswordManager;
import org.commonjava.couch.rbac.Role;
import org.commonjava.couch.rbac.User;
import org.commonjava.web.json.model.Listing;
import org.commonjava.web.test.fixture.TestWarArchiveBuilder;
import org.commonjava.web.user.rest.UserAdminResource;
import org.commonjava.web.user.rest.fixture.TestRESTApplication;
import org.commonjava.web.user.rest.fixture.TestUserManagerConfigProducer;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.gson.reflect.TypeToken;

@RunWith( Arquillian.class )
public class UserAdminResourceTest
    extends AbstractRESTfulUserManagerTest
{

    @Deployment
    public static WebArchive createWar()
    {
        return new TestWarArchiveBuilder( UserAdminResource.class ).withExtraClasses( AbstractRESTfulUserManagerTest.class,
                                                                                      TestRESTApplication.class,
                                                                                      TestUserManagerConfigProducer.class )
                                                                   .withLibrariesIn( new File( "target/dependency" ) )
                                                                   .withLog4jProperties()
                                                                   .build();
    }

    @Inject
    private PasswordManager passwordManager;

    @Before
    public void setupTest()
    {
        fixture.setBasePath( "/test/admin/user" );
        // controls.setDoAuthentication( false );
    }

    @Test
    public void getAdminUser()
        throws Exception
    {
        final User user = fixture.get( fixture.resourceUrl( User.ADMIN ), User.class );

        assertThat( user, notNullValue() );
        assertThat( user.getUsername(), equalTo( User.ADMIN ) );
        assertThat( user.getFirstName(), equalTo( "Admin" ) );
        assertThat( user.getLastName(), equalTo( "User" ) );
        assertThat( user.getEmail(), equalTo( "admin@nowhere.com" ) );

        assertThat( user.getPasswordDigest(), equalTo( passwordManager.digestPassword( "password" ) ) );

        assertThat( user.getRoles(), notNullValue() );
        assertThat( user.getRoles()
                        .size(), equalTo( 1 ) );
        assertThat( user.getRoles()
                        .iterator()
                        .next(), equalTo( Role.ADMIN ) );
    }

    @Test
    public void deleteAdminUser()
        throws Exception
    {
        fixture.delete( fixture.resourceUrl( User.ADMIN ) );
    }

    @Test
    public void createUser()
        throws Exception
    {
        final User user = new User( "test", "test@nowhere.com", "Test", "User", "testPassword" );

        final HttpResponse response = fixture.post( fixture.resourceUrl(), user, HttpStatus.SC_CREATED );
        fixture.assertLocationHeader( response, fixture.resourceUrl( "/test" ) );
    }

    @Test
    public void modifyAdminUser()
        throws Exception
    {
        final User user = fixture.get( fixture.resourceUrl( User.ADMIN ), User.class );

        assertThat( user, notNullValue() );

        user.removeRole( Role.ADMIN );

        System.out.println( "\n\n\n\n\nPOST: " + user + "\n\n\n\n\n" );
        fixture.post( fixture.resourceUrl( User.ADMIN ), user, HttpStatus.SC_OK );
    }

    @Test
    public void createUserThenRetrieveNewAndAdminUsers()
        throws Exception
    {
        final User user = new User( "test", "test@nowhere.com", "Test", "User", "testPassword" );
        fixture.post( fixture.resourceUrl(), user, HttpStatus.SC_CREATED );

        final Listing<User> users = fixture.getListing( fixture.resourceUrl( "/list" ), new TypeToken<Listing<User>>()
        {
        } );

        assertThat( users, notNullValue() );
        assertThat( users.getItems(), notNullValue() );

        User u = users.getItems()
                      .get( 0 );
        assertThat( u.getUsername(), equalTo( "admin" ) );

        u = users.getItems()
                 .get( 1 );
        assertThat( u.getUsername(), equalTo( "test" ) );
    }

}
