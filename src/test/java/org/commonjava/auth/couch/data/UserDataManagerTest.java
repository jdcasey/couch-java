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

import static org.commonjava.couch.test.fixture.LoggingFixture.setupLogging;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.util.Set;

import org.apache.log4j.Level;
import org.commonjava.auth.couch.conf.DefaultUserManagerConfig;
import org.commonjava.auth.couch.model.Permission;
import org.commonjava.auth.couch.model.Role;
import org.commonjava.auth.couch.model.User;
import org.commonjava.couch.conf.DefaultCouchDBConfiguration;
import org.commonjava.couch.db.CouchManager;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class UserDataManagerTest
{

    private static UserDataManager manager;

    private static DefaultUserManagerConfig config;

    private static CouchManager couch;

    @BeforeClass
    public static void setupManager()
    {
        setupLogging( Level.DEBUG );

        config = new DefaultUserManagerConfig();

        couch =
            new CouchManager(
                              new DefaultCouchDBConfiguration(
                                                               "http://developer.commonjava.org/db/test-user-manager" ) );

        manager = new UserDataManager( config, new PasswordManager(), couch );
    }

    @Before
    public void setupDB()
        throws Exception
    {
        if ( couch.dbExists() )
        {
            couch.dropDatabase();
        }
        manager.install();
    }

    @After
    public void teardownDB()
        throws Exception
    {
        couch.dropDatabase();
    }

    @Test
    public void addSimpleUserRolePermission()
        throws Exception
    {
        Permission perm = new Permission( "*" );
        Role role = new Role( "admin", perm );
        User user = new User( "admin", role );

        manager.storePermission( perm );
        manager.storeRole( role );
        manager.storeUser( user );
    }

    @Test
    public void addAndDeleteSimpleUserRolePermission()
        throws Exception
    {
        Permission perm = new Permission( "*" );
        Role role = new Role( "admin", perm );
        User user = new User( "admin", role );

        manager.storePermission( perm );
        manager.storeRole( role );
        manager.storeUser( user );

        manager.deletePermission( perm.getName() );
        manager.deleteRole( role.getName() );
        manager.deleteUser( user.getUsername() );
    }

    @Test
    public void addAndQuerySimpleUserRolePermission()
        throws Exception
    {
        Permission perm = new Permission( "*" );
        Role role = new Role( "admin", perm );
        User user = new User( "admin", role );

        manager.storePermission( perm );
        manager.storeRole( role );
        manager.storeUser( user );

        User u = manager.getUser( user.getUsername() );

        assertThat( u, notNullValue() );
        assertThat( u.getUsername(), equalTo( user.getUsername() ) );

        Role r = manager.getRole( role.getName() );

        assertThat( r, notNullValue() );
        assertThat( r.getName(), equalTo( role.getName() ) );

        Permission p = manager.getPermission( perm.getName() );

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

        manager.storePermission( perm );
        manager.storeRole( role );
        manager.storeUser( user );

        Set<Role> roles = manager.getRoles( user );

        assertThat( roles, notNullValue() );
        assertThat( roles.size(), equalTo( 1 ) );
        assertThat( roles.iterator().next().getName(), equalTo( role.getName() ) );

        Set<Permission> perms = manager.getPermissions( role );

        assertThat( perms, notNullValue() );
        assertThat( perms.size(), equalTo( 1 ) );
        assertThat( perms.iterator().next().getName(), equalTo( perm.getName() ) );
    }

}
