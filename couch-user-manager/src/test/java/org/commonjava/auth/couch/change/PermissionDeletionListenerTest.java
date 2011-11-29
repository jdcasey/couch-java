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
        Role admin = getDataManager().getRole( Role.ADMIN );

        assertThat( admin.getPermissions(), notNullValue() );
        assertThat( admin.getPermissions().size(), equalTo( 1 ) );
        assertThat( admin.getPermissions().iterator().next(), equalTo( Permission.WILDCARD ) );

        getDataManager().deletePermission( Permission.WILDCARD );

        permissionDeletionListener.waitForChange( 20000, 1000 );

        admin = getDataManager().getRole( Role.ADMIN );

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
