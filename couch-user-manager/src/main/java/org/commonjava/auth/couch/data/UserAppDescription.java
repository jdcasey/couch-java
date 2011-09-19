package org.commonjava.auth.couch.data;

import java.util.HashSet;
import java.util.Set;

import org.commonjava.couch.db.model.AppDescription;

public class UserAppDescription
    implements AppDescription
{

    public static final String APP_NAME = "user-logic";

    public enum View
    {
        ALL_USERS( "all-users" ),
        ALL_ROLES( "all-roles" ),
        ALL_PERMISSIONS( "all-permissions" ),
        USER_ROLES( "user-roles" ),
        ROLE_PERMISSIONS( "role-permissions" ),
        ROLE_USERS( "role-users" ),
        PERMISSION_ROLES( "permission-roles" );

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

    private static Set<String> viewNames;

    @Override
    public String getAppName()
    {
        return APP_NAME;
    }

    @Override
    public String getClasspathAppResource()
    {
        return APP_NAME;
    }

    @Override
    public Set<String> getViewNames()
    {
        if ( viewNames == null )
        {
            Set<String> names = new HashSet<String>();
            for ( View view : View.values() )
            {
                names.add( view.viewName() );
            }

            viewNames = names;
        }

        return viewNames;
    }

}
