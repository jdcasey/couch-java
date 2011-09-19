package org.commonjava.couch.conf;

import javax.inject.Named;

import org.commonjava.couch.util.UrlUtils;
import org.commonjava.couch.util.UrlUtils.UrlInfo;
import org.commonjava.web.config.annotation.ConfigNames;
import org.commonjava.web.config.annotation.SectionName;

@SectionName( "database" )
@Named( "dont-use-directly" )
public class DefaultCouchDBConfiguration
    implements CouchDBConfiguration
{

    private final UrlInfo urlInfo;

    private final int maxConnections;

    @ConfigNames( { "url", "maxConnections" } )
    public DefaultCouchDBConfiguration( final String dbUrl, final int maxConnections )
    {
        this.urlInfo = UrlUtils.parseUrlInfo( dbUrl );
        this.maxConnections = maxConnections;
    }

    public DefaultCouchDBConfiguration( final String dbUrl )
    {
        this.urlInfo = UrlUtils.parseUrlInfo( dbUrl );
        this.maxConnections = 20;
    }

    @Override
    public String getDatabaseUrl()
    {
        return urlInfo.getUrl();
    }

    @Override
    public String getDatabaseUser()
    {
        return urlInfo.getUser();
    }

    @Override
    public String getDatabasePassword()
    {
        return urlInfo.getPassword();
    }

    @Override
    public String getDatabaseHost()
    {
        return urlInfo.getHost();
    }

    @Override
    public int getDatabasePort()
    {
        return urlInfo.getPort();
    }

    @Override
    public int getMaxConnections()
    {
        return maxConnections;
    }

}
