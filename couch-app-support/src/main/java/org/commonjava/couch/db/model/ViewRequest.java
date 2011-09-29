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

import static org.commonjava.couch.util.UrlUtils.stringQueryParameter;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class ViewRequest
{

    public static final String WILDCARD = "%7B%7D"; // '{}'

    public static final String KEY = "key";

    public static final String START_KEY = "startkey";

    public static final String END_KEY = "endkey";

    public static final String INCLUDE_DOCS = "include_docs";

    private final String application;

    private final String view;

    private Set<String> views;

    // NOTE: LinkedHashMap is critical, since CouchDB has order-sensitive request parameters.
    private final Map<String, String> requestParameters = new LinkedHashMap<String, String>();

    public ViewRequest( final String application, final String view )
    {
        this.application = application;
        this.view = view;
    }

    public void setFullRangeForBaseKey( final Object baseKey )
    {
        setParameterArray( START_KEY, baseKey );
        setParameterArray( END_KEY, baseKey, WILDCARD );
    }

    public void setParameter( final String key, final Object... values )
    {
        if ( values.length == 1 )
        {
            requestParameters.put( key, stringQueryParameter( values[0] ) );
        }
        else
        {
            setParameterArray( key, values );
        }
    }

    public void setParameterArray( final String key, final Object... values )
    {
        StringBuilder sb = new StringBuilder();

        sb.append( "[" );
        for ( Object val : values )
        {
            if ( sb.length() > 1 )
            {
                sb.append( "," );
            }

            if ( WILDCARD.equals( val ) || ( val instanceof Number ) || ( val instanceof Boolean ) )
            {
                sb.append( val );
            }
            else
            {
                sb.append( stringQueryParameter( val ) );
            }
        }
        sb.append( "]" );

        requestParameters.put( key, sb.toString() );
    }

    public void setParameter( final String key, final boolean value )
    {
        requestParameters.put( key, Boolean.toString( value ) );
    }

    public void setParameter( final String key, final int value )
    {
        requestParameters.put( key, Integer.toString( value ) );
    }

    public void setParameter( final String key, final long value )
    {
        requestParameters.put( key, Long.toString( value ) );
    }

    public String getApplication()
    {
        return application;
    }

    public String getView()
    {
        return view;
    }

    @Override
    public String toString()
    {
        return String.format( "ViewRequest [application=%s, view=%s, parameters=%s]", application,
                              view, requestParameters );
    }

    public Map<String, String> getRequestParameters()
    {
        return requestParameters;
    }

    public void setViews( final Collection<String> views )
    {
        this.views = new HashSet<String>( views );
    }

    public synchronized void addView( final String view )
    {
        if ( views == null )
        {
            views = new HashSet<String>();
        }

        views.add( view );
    }

    public Set<String> getViews()
    {
        Set<String> result = views;
        if ( result == null )
        {
            result = Collections.emptySet();
        }

        return result;
    }

}
