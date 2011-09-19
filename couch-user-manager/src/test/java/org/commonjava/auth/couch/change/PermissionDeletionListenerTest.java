package org.commonjava.auth.couch.change;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.commonjava.auth.couch.model.Permission;
import org.commonjava.auth.couch.model.Role;
import org.junit.Test;

public class PermissionDeletionListenerTest
    extends AbstractUserManagerListenerTest
{

    private PermissionDeletionListener permissionDeletionListener;

    @Test
    public void removeGodPermissionAndReflectChangeInAdminRole()
        throws Exception
    {
        Role admin = dataManager.getRole( Role.ADMIN );

        assertThat( admin.getPermissions(), notNullValue() );
        assertThat( admin.getPermissions().size(), equalTo( 1 ) );
        assertThat( admin.getPermissions().iterator().next(), equalTo( Permission.WILDCARD ) );

        dataManager.deletePermission( Permission.WILDCARD );

        permissionDeletionListener.waitForChange( 20000, 1000 );

        admin = dataManager.getRole( Role.ADMIN );

        boolean result = admin.getPermissions() == null || admin.getPermissions().isEmpty();
        assertThat( result, equalTo( true ) );
    }

    @Override
    protected void setupFixtures()
    {
        permissionDeletionListener =
            weld.instance().select( PermissionDeletionListener.class ).get();
    }
}
