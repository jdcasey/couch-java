package org.commonjava.couch.model;

import static org.apache.commons.lang.StringUtils.join;

import java.util.HashMap;
import java.util.Map;

public class CouchApp
    extends AbstractCouchDocument
{

    private static final String DEFAULT_LANGUAGE = "javascript";

    private static final String DESIGN_PREFIX = "_design/";

    private String language = DEFAULT_LANGUAGE;

    private Map<String, CouchAppView> views;

    public CouchApp( final String id, final Map<String, CouchAppView> views )
    {
        setCouchDocId( DESIGN_PREFIX + id );
        this.views = views;
    }

    public CouchApp( final String id )
    {
        setCouchDocId( DESIGN_PREFIX + id );
        this.views = new HashMap<String, CouchAppView>();
    }

    CouchApp()
    {}

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

}
