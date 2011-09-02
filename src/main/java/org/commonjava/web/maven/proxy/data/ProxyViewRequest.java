package org.commonjava.web.maven.proxy.data;

import org.commonjava.couch.db.model.ViewRequest;
import org.commonjava.web.maven.proxy.conf.ProxyConfiguration;

public class ProxyViewRequest
    extends ViewRequest
{

    public static final String APPLICATION_RESOURCE = "proxy-logic";

    public enum View
    {
        ALL_GROUPS( "all-groups" ), ALL_PROXIES( "all-proxies" ), GROUP_REPOSITORIES(
            "group-repositories" );

        String name;

        private View( final String name )
        {
            this.name = name;
        }

        public String viewName()
        {
            return name;
        }
    }

    public ProxyViewRequest( final ProxyConfiguration config, final View view )
    {
        super( config.getLogicApplication(), view.viewName() );
        setParameter( INCLUDE_DOCS, true );
    }

    public ProxyViewRequest( final ProxyConfiguration config, final View view, final String key )
    {
        this( config, view );
        setParameter( KEY, key );
    }

}
