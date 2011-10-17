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
package org.commonjava.couch.conf;

import javax.enterprise.inject.Alternative;
import javax.inject.Named;

import org.commonjava.couch.util.UrlUtils;
import org.commonjava.couch.util.UrlUtils.UrlInfo;
import org.commonjava.web.config.annotation.ConfigName;
import org.commonjava.web.config.annotation.SectionName;

@SectionName( "database" )
@Alternative
@Named( "do-not-use-directly" )
public class DefaultCouchDBConfiguration
    implements CouchDBConfiguration
{

    private static final int DEFAULT_MAX_CONNECTIONS = 20;

    private UrlInfo urlInfo;

    private int maxConnections;

    public DefaultCouchDBConfiguration( final String dbUrl, final int maxConnections )
    {
        this.urlInfo = UrlUtils.parseUrlInfo( dbUrl );
        this.maxConnections = maxConnections;
    }

    public DefaultCouchDBConfiguration( final String dbUrl )
    {
        this.urlInfo = UrlUtils.parseUrlInfo( dbUrl );
        this.maxConnections = -1;
    }

    public DefaultCouchDBConfiguration()
    {}

    @ConfigName( "url" )
    public void setDatabaseUrl( final String databaseUrl )
    {
        this.urlInfo = UrlUtils.parseUrlInfo( databaseUrl );
    }

    @ConfigName( "connections.max" )
    public void setMaxConnections( final int maxConnections )
    {
        this.maxConnections = maxConnections;
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
        return maxConnections < 1 ? DEFAULT_MAX_CONNECTIONS : maxConnections;
    }

}
