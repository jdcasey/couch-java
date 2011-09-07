package org.commonjava.auth.shiro.couch.web;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;

import org.apache.shiro.util.AbstractFactory;
import org.apache.shiro.web.filter.mgt.FilterChainManager;
import org.apache.shiro.web.filter.mgt.FilterChainResolver;
import org.apache.shiro.web.filter.mgt.PathMatchingFilterChainResolver;
import org.apache.shiro.web.servlet.AbstractShiroFilter;
import org.commonjava.auth.shiro.couch.CouchRealm;

/**
 * See http://shiro.apache.org/web.html#Web-defaultfilters
 * 
 * @author jdcasey
 */
public class CouchShiroFilter
    extends AbstractShiroFilter
{

    public static final String AUTHC_BASIC = "authcBasic";

    public static final String REST = "rest";

    private static final Map<String, String> URL_CHAIN_MAP = new HashMap<String, String>()
    {
        {
            put( "/*", AUTHC_BASIC );
        }

        private static final long serialVersionUID = 1L;
    };

    @Inject
    private CouchRealm realm;

    protected Map<String, String> getUrlChainMappings()
    {
        return URL_CHAIN_MAP;
    }

    protected void configureFilterChainResolver()
    {
        FilterChainResolverFactory filterChainResolverFactory =
            new FilterChainResolverFactory( this );

        FilterChainResolver resolver = filterChainResolverFactory.getInstance();
        setFilterChainResolver( resolver );
    }

    @Override
    public void init()
        throws ServletException
    {
        if ( realm == null )
        {
            throw new ServletException(
                                        "Failed to initialize security. Realm has not been injected!" );
        }

        realm.setupSecurityManager();
        configureFilterChainResolver();
    }

    protected static final class FilterChainResolverFactory
        extends AbstractFactory<FilterChainResolver>
    {

        private final CouchShiroFilter filter;

        public FilterChainResolverFactory( final CouchShiroFilter filter )
        {
            this.filter = filter;
        }

        @Override
        protected FilterChainResolver createInstance()
        {
            FilterChainResolver filterChainResolver;

            FilterConfig filterConfig = filter.getFilterConfig();
            if ( filterConfig != null )
            {
                filterChainResolver = new PathMatchingFilterChainResolver( filterConfig );
            }
            else
            {
                filterChainResolver = new PathMatchingFilterChainResolver();
            }

            if ( filterChainResolver instanceof PathMatchingFilterChainResolver )
            {
                PathMatchingFilterChainResolver resolver =
                    (PathMatchingFilterChainResolver) filterChainResolver;

                FilterChainManager manager = resolver.getFilterChainManager();

                for ( Map.Entry<String, String> entry : filter.getUrlChainMappings().entrySet() )
                {
                    String path = entry.getKey();
                    String chain = entry.getValue();
                    manager.createChain( path, chain );
                }
            }

            return filterChainResolver;
        }
    }

}
