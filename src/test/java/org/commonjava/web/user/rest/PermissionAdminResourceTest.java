package org.commonjava.web.user.rest;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.apache.http.HttpStatus;
import org.commonjava.auth.couch.model.Permission;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith( Arquillian.class )
public class PermissionAdminResourceTest
    extends AbstractRESTUserManagerTest
{

    private static final String BASE_URL = "http://localhost:8080/test/admin/permission";

    @Test
    public void getGodPermission()
        throws Exception
    {
        Permission perm = get( BASE_URL + "/*", Permission.class );

        assertThat( perm, notNullValue() );
        assertThat( perm.getName(), equalTo( "*" ) );
    }

    @Test
    public void deleteGodPermission()
        throws Exception
    {
        delete( BASE_URL + "/*" );
    }

    @Test
    public void createPermission()
        throws Exception
    {
        post( BASE_URL, new Permission( "test", Permission.READ ), HttpStatus.SC_CREATED );
    }

}
