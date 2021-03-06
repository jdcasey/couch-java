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

    public static final String LIMIT = "limit";

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

    public void setKey( final Object key )
    {
        setParameter( KEY, key );
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
        final StringBuilder sb = new StringBuilder();

        sb.append( "[" );
        for ( final Object val : values )
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
        return String.format( "ViewRequest [application=%s, view=%s, parameters=%s]", application, view,
                              requestParameters );
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
