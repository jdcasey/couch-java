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
package org.commonjava.auth.couch.data;

import static org.commonjava.couch.test.fixture.LoggingFixture.setupLogging;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.util.Set;

import org.apache.log4j.Level;
import org.commonjava.auth.couch.fixture.CouchUserFixture;
import org.commonjava.couch.rbac.Permission;
import org.commonjava.couch.rbac.Role;
import org.commonjava.couch.rbac.User;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

public class UserDataManagerTest
{

    @Rule
    public CouchUserFixture fixture = new CouchUserFixture();

    @BeforeClass
    public static void logging()
    {
        setupLogging( Level.INFO );
    }

    @Test
    public void addSimpleUserRolePermission()
        throws Exception
    {
        Permission perm = new Permission( "*" );
        Role role = new Role( "admin", perm );
        User user = new User( "admin", role );

        getManager().storePermission( perm );
        getManager().storeRole( role );
        getManager().storeUser( user );
    }

    private UserDataManager getManager()
    {
        return fixture.getUserDataManager();
    }

    @Test
    public void addAndDeleteSimpleUserRolePermission()
        throws Exception
    {
        Permission perm = new Permission( "*" );
        Role role = new Role( "admin", perm );
        User user = new User( "admin", role );

        getManager().storePermission( perm );
        getManager().storeRole( role );
        getManager().storeUser( user );

        getManager().deletePermission( perm.getName() );
        getManager().deleteRole( role.getName() );
        getManager().deleteUser( user.getUsername() );
    }

    @Test
    public void addAndQuerySimpleUserRolePermission()
        throws Exception
    {
        Permission perm = new Permission( "*" );
        Role role = new Role( "admin", perm );
        User user = new User( "admin", role );

        getManager().storePermission( perm );
        getManager().storeRole( role );
        getManager().storeUser( user );

        User u = getManager().getUser( user.getUsername() );

        assertThat( u, notNullValue() );
        assertThat( u.getUsername(), equalTo( user.getUsername() ) );

        Role r = getManager().getRole( role.getName() );

        assertThat( r, notNullValue() );
        assertThat( r.getName(), equalTo( role.getName() ) );

        Permission p = getManager().getPermission( perm.getName() );

        assertThat( p, notNullValue() );
        assertThat( p.getName(), equalTo( perm.getName() ) );
    }

    @Test
    public void addAndQueryByAssociation_SimpleUserRolePermission()
        throws Exception
    {
        Permission perm = new Permission( "*" );
        Role role = new Role( "admin", perm );
        User user = new User( "admin", role );

        getManager().storePermission( perm );
        getManager().storeRole( role );
        getManager().storeUser( user );

        Set<Role> roles = getManager().getRoles( user );

        assertThat( roles, notNullValue() );
        assertThat( roles.size(), equalTo( 1 ) );
        assertThat( roles.iterator().next().getName(), equalTo( role.getName() ) );

        Set<Permission> perms = getManager().getPermissions( role );

        assertThat( perms, notNullValue() );
        assertThat( perms.size(), equalTo( 1 ) );
        assertThat( perms.iterator().next().getName(), equalTo( perm.getName() ) );
    }

}
