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
package org.commonjava.couch.util;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

public final class UrlUtils
{

    private UrlUtils()
    {}

    public static String stringQueryParameter( final Object value )
    {
        String base = String.valueOf( value );
        return "%22" + base + "%22";
    }

    public static String buildUrl( final String baseUrl, final String... parts )
        throws MalformedURLException
    {
        return buildUrl( baseUrl, null, parts );
    }

    public static String buildUrl( final String baseUrl, final Map<String, String> params,
                                   final String... parts )
        throws MalformedURLException
    {
        if ( parts == null || parts.length < 1 )
        {
            return baseUrl;
        }

        StringBuilder urlBuilder = new StringBuilder();

        if ( !parts[0].startsWith( baseUrl ) )
        {
            urlBuilder.append( baseUrl );
        }

        for ( String part : parts )
        {
            if ( part.startsWith( "/" ) )
            {
                part = part.substring( 1 );
            }

            if ( urlBuilder.length() > 0 && urlBuilder.charAt( urlBuilder.length() - 1 ) != '/' )
            {
                urlBuilder.append( "/" );
            }

            urlBuilder.append( part );
        }

        if ( params != null && !params.isEmpty() )
        {
            urlBuilder.append( "?" );
            boolean first = true;
            for ( Map.Entry<String, String> param : params.entrySet() )
            {
                if ( first )
                {
                    first = false;
                }
                else
                {
                    urlBuilder.append( "&" );
                }

                urlBuilder.append( param.getKey() ).append( "=" ).append( param.getValue() );
            }
        }

        return new URL( urlBuilder.toString() ).toExternalForm();
    }

    public static UrlInfo parseUrlInfo( final String url )
    {
        return new UrlInfo( url );
    }

    public static final class UrlInfo
    {
        private final String url;

        private String user;

        private String password;

        private final String host;

        private int port;

        private UrlInfo( final String u )
        {
            String resultUrl = u;

            URL url;
            try
            {
                url = new URL( u );
            }
            catch ( MalformedURLException e )
            {
                throw new IllegalArgumentException( "Failed to parse repository URL: '" + u
                    + "'. Reason: " + e.getMessage(), e );
            }

            String userInfo = url.getUserInfo();
            if ( userInfo != null && user == null && password == null )
            {
                user = userInfo;
                password = null;

                int idx = userInfo.indexOf( ':' );
                if ( idx > 0 )
                {
                    user = userInfo.substring( 0, idx );
                    password = userInfo.substring( idx + 1 );

                    StringBuilder sb = new StringBuilder();
                    idx = this.url.indexOf( "://" );
                    sb.append( this.url.substring( 0, idx + 3 ) );

                    idx = this.url.indexOf( "@" );
                    if ( idx > 0 )
                    {
                        sb.append( this.url.substring( idx + 1 ) );
                    }

                    resultUrl = sb.toString();
                }
            }

            this.url = resultUrl;

            host = url.getHost();
            if ( url.getPort() < 0 )
            {
                port = url.getProtocol().equals( "https" ) ? 443 : 80;
            }
            else
            {
                port = url.getPort();
            }
        }

        public String getUrl()
        {
            return url;
        }

        public String getUser()
        {
            return user;
        }

        public String getPassword()
        {
            return password;
        }

        public String getHost()
        {
            return host;
        }

        public int getPort()
        {
            return port;
        }

    }
}
