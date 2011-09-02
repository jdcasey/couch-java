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

import org.commonjava.auth.couch.conf.UserManagerConfiguration;
import org.commonjava.couch.db.model.ViewRequest;

public class UserViewRequest
    extends ViewRequest
{

    public static final String APPLICATION_RESOURCE = "user-logic";

    public enum View
    {
        USER_ROLES( "user-roles" ), ROLE_PERMISSIONS( "role-permissions" );

        String name;

        private View( final String name )
        {
            this.name = name;
        }

        public String viewName()
        {
            return name;
        }
    }

    public UserViewRequest( final UserManagerConfiguration config, final View view )
    {
        super( config.getLogicApplication(), view.viewName() );
        setParameter( INCLUDE_DOCS, true );
    }

}
