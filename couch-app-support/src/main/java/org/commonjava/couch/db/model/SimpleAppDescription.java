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
