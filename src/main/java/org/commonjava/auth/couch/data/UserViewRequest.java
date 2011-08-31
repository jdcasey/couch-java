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
