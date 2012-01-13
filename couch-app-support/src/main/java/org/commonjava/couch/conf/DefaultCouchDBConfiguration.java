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
package org.commonjava.couch.conf;

import javax.enterprise.inject.Alternative;
import javax.inject.Named;

import org.commonjava.couch.util.UrlInfo;
import org.commonjava.couch.util.UrlUtils;
import org.commonjava.web.config.annotation.ConfigName;
import org.commonjava.web.config.annotation.SectionName;
import org.commonjava.web.config.section.ConfigurationSectionListener;

@Alternative
@Named( "do-not-use-directly" )
@SectionName( ConfigurationSectionListener.DEFAULT_SECTION )
public class DefaultCouchDBConfiguration
    implements CouchDBConfiguration
{

    private static final int DEFAULT_MAX_CONNECTIONS = 20;

    private UrlInfo urlInfo;

    private int maxConnections = -1;

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

    public DefaultCouchDBConfiguration( final CouchDBConfiguration srcConfig, final String dbName )
    {
        final String url = UrlUtils.siblingDatabaseUrl( srcConfig.getDatabaseUrlInfo()
                                                                 .getRawUrl(), dbName );
        this.urlInfo = UrlUtils.parseUrlInfo( url );
        this.maxConnections = srcConfig.getMaxConnections();
    }

    public DefaultCouchDBConfiguration( final CouchDBConfiguration srcConfig, final String dbName,
                                        final int maxConnections )
    {
        final String url = UrlUtils.siblingDatabaseUrl( srcConfig.getDatabaseUrlInfo()
                                                                 .getRawUrl(), dbName );
        this.urlInfo = UrlUtils.parseUrlInfo( url );
        this.maxConnections = maxConnections;
    }

    public DefaultCouchDBConfiguration()
    {
    }

    @ConfigName( "db.url" )
    public void setDatabaseUrl( final String databaseUrl )
    {
        this.urlInfo = UrlUtils.parseUrlInfo( databaseUrl );
    }

    @ConfigName( "db.connections.max" )
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

    @Override
    public UrlInfo getDatabaseUrlInfo()
    {
        return urlInfo;
    }

}
