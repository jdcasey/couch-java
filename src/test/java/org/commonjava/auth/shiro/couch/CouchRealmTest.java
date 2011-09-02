package org.commonjava.auth.shiro.couch;

import static org.commonjava.auth.shiro.couch.fixture.LoggingFixture.setupLogging;

import org.apache.log4j.Level;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.subject.Subject;
import org.commonjava.auth.couch.conf.DefaultUserManagerConfig;
import org.commonjava.auth.couch.data.PasswordManager;
import org.commonjava.auth.couch.data.UserDataManager;
import org.commonjava.auth.couch.model.Permission;
import org.commonjava.auth.couch.model.Role;
import org.commonjava.auth.couch.model.User;
import org.commonjava.auth.shiro.couch.model.ShiroUser;
import org.commonjava.couch.db.CouchManager;
import org.commonjava.couch.model.io.CouchAppReader;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class CouchRealmTest
{

    private static UserDataManager manager;

    private static DefaultUserManagerConfig config;

    private static CouchManager couch;

    private static PasswordManager passwordManager;

    private static CouchRealm realm;

    @BeforeClass
    public static void initTestClass()
    {
        setupLogging( Level.DEBUG );

        passwordManager = new PasswordManager();

        config = new DefaultUserManagerConfig();
        config.setDatabaseUrl( "http://developer.commonjava.org/db/test-shiro" );
        config.setAdminEmail( "admin@nowhere.com" );
        config.setAdminFirstName( "Admin" );
        config.setAdminLastName( "User" );
        config.setAdminPassword( "password" );

        couch = new CouchManager();

        CouchAppReader reader = new CouchAppReader();

        manager = new UserDataManager( config, couch, reader );

        realm = new CouchRealm( manager );

        CouchPermissionResolver resolver = new CouchPermissionResolver( manager );
        realm.setRolePermissionResolver( resolver );
        realm.setPermissionResolver( resolver );

        SecurityUtils.setSecurityManager( new DefaultSecurityManager( realm ) );
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

        Permission perm = new Permission( "*" );
        Role role = new Role( "admin", perm );

        User user = config.createInitialAdminUser( passwordManager );
        user.addRole( role.getName() );

        System.out.printf( "Creating admin user: %s\n", user );

        manager.storePermission( perm );
        manager.storeRole( role );
        manager.storeUser( user );
    }

    @After
    public void teardownDB()
        throws Exception
    {
        couch.dropDatabase( config.getDatabaseUrl() );
    }

    @Test
    public void login()
    {
        Subject subject = SecurityUtils.getSubject();

        User user = new User( User.ADMIN );
        user.setPasswordDigest( passwordManager.digestPassword( "password" ) );

        subject.login( ShiroUser.getAuthenticationToken( user ) );

        System.out.println( subject );
    }

    @Test
    public void checkMinorPermissionVsAdminUniversalPermission()
        throws Exception
    {
        Subject subject = SecurityUtils.getSubject();

        User user = new User( User.ADMIN );
        user.setPasswordDigest( passwordManager.digestPassword( "password" ) );
        subject.login( ShiroUser.getAuthenticationToken( user ) );

        Permission perm = new Permission( "test", "read" );
        manager.storePermission( perm );

        subject.checkPermission( "test:read" );

        System.out.println( subject );
    }

}
