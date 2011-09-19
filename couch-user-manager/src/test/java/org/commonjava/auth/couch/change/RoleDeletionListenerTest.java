package org.commonjava.auth.couch.change;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.commonjava.auth.couch.model.Role;
import org.commonjava.auth.couch.model.User;
import org.junit.Test;

public class RoleDeletionListenerTest
    extends AbstractUserManagerListenerTest
{

    private RoleDeletionListener roleDeletionListener;

    @Test
    public void removeAdminRoleAndReflectChangeInAdminUser()
        throws Exception
    {
        User admin = dataManager.getUser( User.ADMIN );

        assertThat( admin.getRoles(), notNullValue() );
        assertThat( admin.getRoles().size(), equalTo( 1 ) );
        assertThat( admin.getRoles().iterator().next(), equalTo( Role.ADMIN ) );

        dataManager.deleteRole( Role.ADMIN );

        roleDeletionListener.waitForChange( 20000, 1000 );

        admin = dataManager.getUser( User.ADMIN );

        boolean result = admin.getRoles() == null || admin.getRoles().isEmpty();
        assertThat( result, equalTo( true ) );
    }

    @Override
    protected void setupFixtures()
    {
        roleDeletionListener = weld.instance().select( RoleDeletionListener.class ).get();
    }

}
