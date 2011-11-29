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
