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
