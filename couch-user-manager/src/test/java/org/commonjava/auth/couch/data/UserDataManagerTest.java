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
package org.commonjava.auth.couch.data;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.util.Set;

import org.commonjava.auth.couch.fixture.CouchUserFixture;
import org.commonjava.auth.couch.model.Permission;
import org.commonjava.auth.couch.model.Role;
import org.commonjava.auth.couch.model.User;
import org.junit.Rule;
import org.junit.Test;

public class UserDataManagerTest
{

    @Rule
    public CouchUserFixture fixture = new CouchUserFixture();

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
