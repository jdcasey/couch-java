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
package org.commonjava.couch.model.io;

import static org.apache.commons.io.IOUtils.closeQuietly;
import static org.apache.commons.io.IOUtils.copy;
import static org.apache.commons.io.IOUtils.readLines;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.List;

import org.codehaus.plexus.component.annotations.Component;
import org.commonjava.couch.model.CouchApp;
import org.commonjava.couch.model.CouchAppView;

@Component( role = CouchAppReader.class )
public class CouchAppReader
{
    private static final String APP_BASEPATH = "couchapps/";

    private static final String VIEW_SUBPATH = "/views/";

    private static final String VIEWS_LISTING = "/views.lst";

    private static final String MAP_JS = "/map.js";

    private static final String REDUCE_JS = "/reduce.js";

    public CouchApp readAppDefinition( final String appName )
        throws IOException
    {
        return readAppDefinition( appName, appName );
    }

    public CouchApp readAppDefinition( final String appName, final String appResource )
        throws IOException
    {
        ClassLoader cloader = Thread.currentThread().getContextClassLoader();

        CouchApp app = new CouchApp( appName );

        String appBase = APP_BASEPATH + appName;
        String viewListing = appBase + VIEWS_LISTING;

        List<String> listing = null;
        InputStream in = cloader.getResourceAsStream( viewListing );
        if ( in != null )
        {
            try
            {
                listing = readLines( in );
            }
            finally
            {
                closeQuietly( in );
            }
        }

        String viewBase = appBase + VIEW_SUBPATH;
        if ( listing != null )
        {
            for ( String view : listing )
            {
                String mapPath = viewBase + view + MAP_JS;
                String reducePath = viewBase + view + REDUCE_JS;

                String map = null;

                in = cloader.getResourceAsStream( mapPath );
                StringWriter sWriter = new StringWriter();
                if ( in != null )
                {
                    try
                    {
                        copy( in, sWriter );
                    }
                    finally
                    {
                        closeQuietly( in );
                    }
                    map = sWriter.toString();
                }

                String reduce = null;

                in = cloader.getResourceAsStream( reducePath );
                sWriter = new StringWriter();
                if ( in != null )
                {
                    try
                    {
                        copy( in, sWriter );
                    }
                    finally
                    {
                        closeQuietly( in );
                    }
                    reduce = sWriter.toString();
                }

                app.addView( view, new CouchAppView( map, reduce ) );
            }
        }

        return app;
    }

}
