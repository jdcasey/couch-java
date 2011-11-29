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
package org.commonjava.couch.model;

import static org.apache.commons.lang.StringUtils.join;

import java.util.HashMap;
import java.util.Map;

import org.commonjava.couch.db.model.AppDescription;

public class CouchApp
    extends AbstractCouchDocument
{

    private static final String DEFAULT_LANGUAGE = "javascript";

    private static final String DESIGN_PREFIX = "_design/";

    private String language = DEFAULT_LANGUAGE;

    private Map<String, CouchAppView> views;

    private transient final AppDescription description;

    public CouchApp( final String id, final Map<String, CouchAppView> views,
                     final AppDescription description )
    {
        this.description = description;
        setCouchDocId( DESIGN_PREFIX + id );
        this.views = views;
    }

    public CouchApp( final String id, final AppDescription description )
    {
        this.description = description;
        setCouchDocId( DESIGN_PREFIX + id );
        this.views = new HashMap<String, CouchAppView>();
    }

    public String getLanguage()
    {
        return language;
    }

    void setLanguage( final String language )
    {
        this.language = language;
    }

    public synchronized CouchAppView addView( final String name, final CouchAppView view )
    {
        if ( views == null )
        {
            views = new HashMap<String, CouchAppView>();
        }

        return views.put( name, view );
    }

    public Map<String, CouchAppView> getViews()
    {
        return views;
    }

    void setViews( final Map<String, CouchAppView> views )
    {
        this.views = views;
    }

    @Override
    public String toString()
    {
        return String.format( "CouchApp [id=%s]\nViews:\n\n%s", getCouchDocId(),
                              join( views.keySet(), "\n" ) );
    }

    public AppDescription getDescription()
    {
        return description;
    }

}
