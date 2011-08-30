package org.commonjava.couch.db.model;

import java.util.HashMap;
import java.util.Map;

public class ViewRequest
{

    private final String application;

    private final String view;

    private final Map<String, String> requestParameters = new HashMap<String, String>();

    public ViewRequest( final String application, final String view )
    {
        this.application = application;
        this.view = view;
    }

    public void setParameter( final String key, final Object value )
    {
        requestParameters.put( key, stringParam( value ) );
    }

    public void setParameter( final String key, final boolean value )
    {
        requestParameters.put( key, Boolean.toString( value ) );
    }

    protected String stringParam( final Object value )
    {
        String base = String.valueOf( value );
        return "%22" + base + "%22";
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

}
