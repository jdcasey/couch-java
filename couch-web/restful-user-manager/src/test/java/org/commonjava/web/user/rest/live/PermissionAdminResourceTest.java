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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.log4j.Level;
import org.commonjava.couch.rbac.Permission;
import org.commonjava.couch.test.fixture.LoggingFixture;
import org.commonjava.web.json.model.Listing;
import org.commonjava.web.test.fixture.TestWarArchiveBuilder;
import org.commonjava.web.user.rest.PermissionAdminResource;
import org.commonjava.web.user.rest.fixture.TestRESTApplication;
import org.commonjava.web.user.rest.fixture.TestUserManagerConfigProducer;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.gson.reflect.TypeToken;

@RunWith( Arquillian.class )
public class PermissionAdminResourceTest
    extends AbstractRESTfulUserManagerTest
{

    @BeforeClass
    public static void setup()
    {
        createWar();
    }

    @Before
    public void setupTest()
    {
        fixture.setBasePath( "/test/admin/permission" );
        // controls.setDoAuthentication( false );
    }

    @Deployment
    public static WebArchive createWar()
    {
        LoggingFixture.setupLogging( Level.DEBUG );
        return new TestWarArchiveBuilder( PermissionAdminResource.class ).withExtraClasses( AbstractRESTfulUserManagerTest.class,
                                                                                            TestRESTApplication.class,
                                                                                            TestUserManagerConfigProducer.class )
                                                                         .withLibrariesIn( new File(
                                                                                                     "target/dependency" ) )
                                                                         .withLog4jProperties()
                                                                         .build();
    }

    @Test
    public void getGodPermission()
        throws Exception
    {
        final Permission perm = fixture.get( fixture.resourceUrl( "/*" ), Permission.class );

        assertThat( perm, notNullValue() );
        assertThat( perm.getName(), equalTo( "*" ) );
    }

    @Test
    public void deleteGodPermission()
        throws Exception
    {
        fixture.delete( fixture.resourceUrl( "/*" ) );
    }

    @Test
    public void createPermission()
        throws Exception
    {
        final HttpResponse response =
            fixture.post( fixture.resourceUrl(), new Permission( "test", Permission.READ ), HttpStatus.SC_CREATED );

        fixture.assertLocationHeader( response, fixture.resourceUrl( "/test:read" ) );
    }

    @Test
    public void createPermissionThenReadGodAndNewPermissions()
        throws Exception
    {
        fixture.post( fixture.resourceUrl(), new Permission( "test", Permission.READ ), HttpStatus.SC_CREATED );

        final Listing<Permission> listing =
            fixture.getListing( fixture.resourceUrl( "/list" ), new TypeToken<Listing<Permission>>()
            {
            } );

        assertThat( listing, notNullValue() );

        final List<? extends Permission> items = listing.getItems();
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
