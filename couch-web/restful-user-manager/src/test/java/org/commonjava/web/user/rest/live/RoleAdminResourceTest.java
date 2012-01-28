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
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.gson.reflect.TypeToken;

@RunWith( Arquillian.class )
public class RoleAdminResourceTest
    extends AbstractRESTfulUserManagerTest
{

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

    @Before
    public void setupTest()
    {
        fixture.setBasePath( "/test/admin/role" );
        // controls.setDoAuthentication( false );
    }

    @Test
    public void getAdminRole()
        throws Exception
    {
        final Role role = fixture.get( fixture.resourceUrl( "/admin" ), Role.class );

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
        fixture.delete( fixture.resourceUrl( "/admin" ) );
    }

    @Test
    public void createRole()
        throws Exception
    {
        final Role r = new Role( "test", new Permission( Permission.WILDCARD ) );

        final HttpResponse response = fixture.post( fixture.resourceUrl(), r, HttpStatus.SC_CREATED );
        fixture.assertLocationHeader( response, fixture.resourceUrl( "/test" ) );
    }

    @Test
    public void modifyAdminRole()
        throws Exception
    {
        final RoleDoc role = fixture.get( fixture.resourceUrl( "/admin" ), RoleDoc.class );

        assertThat( role, notNullValue() );

        role.removePermission( Permission.WILDCARD );
        fixture.post( fixture.resourceUrl( "/admin" ), role, HttpStatus.SC_OK );
    }

    @Test
    public void createRoleThenGetNewAndAdminRoles()
        throws Exception
    {
        final HttpResponse response =
            fixture.post( fixture.resourceUrl(), new Role( "test", new Permission( Permission.WILDCARD ) ),
                          HttpStatus.SC_CREATED );

        fixture.assertLocationHeader( response, fixture.resourceUrl( "/test" ) );

        final Listing<Role> roles = fixture.getListing( fixture.resourceUrl( "/list" ), new TypeToken<Listing<Role>>()
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
