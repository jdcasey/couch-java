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

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.commonjava.auth.couch.model.RoleDoc;
import org.commonjava.couch.rbac.Permission;
import org.commonjava.couch.rbac.Role;
import org.commonjava.web.common.model.Listing;
import org.commonjava.web.test.fixture.TestWarArchiveBuilder;
import org.commonjava.web.user.rest.RoleAdminResource;
import org.commonjava.web.user.rest.fixture.TestRESTApplication;
import org.commonjava.web.user.rest.fixture.TestUserManagerConfigProducer;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.gson.reflect.TypeToken;

@RunWith( Arquillian.class )
public class RoleAdminResourceTest
    extends AbstractRESTfulUserManagerTest
{

    private static final String BASE_URL = "http://localhost:8080/test/admin/role";

    @Deployment
    public static WebArchive createWar()
    {
        return new TestWarArchiveBuilder( RoleAdminResource.class ).withExtraClasses( AbstractRESTfulUserManagerTest.class,
                                                                                      TestRESTApplication.class,
                                                                                      TestUserManagerConfigProducer.class )
                                                                   .withLibrariesIn( new File( "target/dependency" ) )
                                                                   .withLog4jProperties()
                                                                   .build();
    }

    @Test
    public void getAdminRole()
        throws Exception
    {
        final Role role = get( BASE_URL + "/admin", Role.class );

        assertThat( role, notNullValue() );
        assertThat( role.getName(), equalTo( "admin" ) );
        assertThat( role.getPermissions(), notNullValue() );
        assertThat( role.getPermissions()
                        .size(), equalTo( 1 ) );
        assertThat( role.getPermissions()
                        .iterator()
                        .next(), equalTo( "*" ) );
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
        final Role r = new Role( "test", new Permission( Permission.WILDCARD ) );

        final HttpResponse response = post( BASE_URL, r, HttpStatus.SC_CREATED );
        assertLocationHeader( response, BASE_URL + "/test" );
    }

    @Test
    public void modifyAdminRole()
        throws Exception
    {
        final RoleDoc role = get( BASE_URL + "/admin", RoleDoc.class );

        assertThat( role, notNullValue() );

        role.removePermission( Permission.WILDCARD );
        post( BASE_URL + "/admin", role, HttpStatus.SC_OK );
    }

    @Test
    public void createRoleThenGetNewAndAdminRoles()
        throws Exception
    {
        final HttpResponse response =
            post( BASE_URL, new Role( "test", new Permission( Permission.WILDCARD ) ), HttpStatus.SC_CREATED );

        assertLocationHeader( response, BASE_URL + "/test" );

        final Listing<Role> roles = getListing( BASE_URL + "/list", new TypeToken<Listing<Role>>()
        {
        } );

        assertThat( roles, notNullValue() );
        assertThat( roles.getItems(), notNullValue() );

        Role r = roles.getItems()
                      .get( 0 );
        assertThat( r.getName(), equalTo( "admin" ) );

        r = roles.getItems()
                 .get( 1 );
        assertThat( r.getName(), equalTo( "test" ) );

    }
}
