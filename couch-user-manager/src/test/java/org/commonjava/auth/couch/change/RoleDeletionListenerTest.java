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
        User admin = getDataManager().getUser( User.ADMIN );

        assertThat( admin.getRoles(), notNullValue() );
        assertThat( admin.getRoles().size(), equalTo( 1 ) );
        assertThat( admin.getRoles().iterator().next(), equalTo( Role.ADMIN ) );

        getDataManager().deleteRole( Role.ADMIN );

        roleDeletionListener.waitForChange( 20000, 1000 );

        admin = getDataManager().getUser( User.ADMIN );

        boolean result = admin.getRoles() == null || admin.getRoles().isEmpty();
        assertThat( result, equalTo( true ) );
    }

    @Override
    protected void setupFixtures()
    {
        roleDeletionListener = weld.instance().select( RoleDeletionListener.class ).get();
    }

}
