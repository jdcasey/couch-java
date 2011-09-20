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
