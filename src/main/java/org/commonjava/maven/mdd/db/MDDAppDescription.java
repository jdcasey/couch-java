package org.commonjava.maven.mdd.db;

import java.util.HashSet;
import java.util.Set;

import org.commonjava.couch.db.model.AppDescription;

public class MDDAppDescription
    implements AppDescription
{

    public static final String APP_NAME = "dep-logic";

    public enum View
    {
        DIRECT_DEPENDENCIES( "direct-dependencies" ), DIRECT_DEPENDENTS( "direct-dependents" );

        private final String viewName;

        private View( final String viewName )
        {
            this.viewName = viewName;
        }

        public String viewName()
        {
            return viewName;
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
