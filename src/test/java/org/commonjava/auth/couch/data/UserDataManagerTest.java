package org.commonjava.auth.couch.data;

import static org.commonjava.auth.couch.fixture.LoggingFixture.setupLogging;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.util.Set;

import org.apache.log4j.Level;
import org.commonjava.auth.couch.conf.DefaultUserManagerConfig;
import org.commonjava.auth.couch.model.Permission;
import org.commonjava.auth.couch.model.Role;
import org.commonjava.auth.couch.model.User;
import org.commonjava.couch.db.CouchManager;
import org.commonjava.couch.model.io.CouchAppReader;
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
        config.setDatabaseUrl( "http://developer.commonjava.org/db/test-user-manager" );

        couch = new CouchManager();

        CouchAppReader reader = new CouchAppReader();

        manager = new UserDataManager( config, couch, reader );
    }

    @Before
    public void setupDB()
        throws Exception
    {
        if ( couch.dbExists( config.getDatabaseUrl() ) )
        {
            couch.dropDatabase( config.getDatabaseUrl() );
        }
        manager.install();
    }

    @After
    public void teardownDB()
        throws Exception
    {
        couch.dropDatabase( config.getDatabaseUrl() );
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
