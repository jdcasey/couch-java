package org.commonjava.couch.db.model;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class SimpleAppDescription
    implements AppDescription
{

    private final String appName;

    private final String appResource;

    private final Set<String> viewNames = new HashSet<String>();

    public SimpleAppDescription( final String appName )
    {
        this( appName, appName );
    }

    public SimpleAppDescription( final String appName, final String appResource,
                                 final String... views )
    {
        this.appName = appName;
        this.appResource = appResource;
        this.viewNames.addAll( Arrays.asList( views ) );
    }

    @Override
    public String getAppName()
    {
        return appName;
    }

    @Override
    public String getClasspathAppResource()
    {
        return appResource;
    }

    public void setViewNames( final Collection<String> viewNames )
    {
        this.viewNames.clear();
        this.viewNames.addAll( viewNames );
    }

    public boolean addViewName( final String viewName )
    {
        return viewNames.add( viewName );
    }

    @Override
    public Set<String> getViewNames()
    {
        return viewNames;
    }

}
