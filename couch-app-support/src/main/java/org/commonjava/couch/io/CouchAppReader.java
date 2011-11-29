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
package org.commonjava.couch.io;

import static org.apache.commons.io.IOUtils.closeQuietly;
import static org.apache.commons.io.IOUtils.copy;
import static org.apache.commons.io.IOUtils.readLines;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.List;
import java.util.Set;

import javax.inject.Singleton;

import org.commonjava.couch.db.CouchDBException;
import org.commonjava.couch.db.model.AppDescription;
import org.commonjava.couch.model.CouchApp;
import org.commonjava.couch.model.CouchAppView;

@Singleton
public class CouchAppReader
{
    private static final String APP_BASEPATH = "couchapps/";

    private static final String VIEW_SUBPATH = "/views/";

    private static final String MAP_JS = "/map.js";

    private static final String REDUCE_JS = "/reduce.js";

    public CouchApp readAppDefinition( final AppDescription description )
        throws IOException, CouchDBException
    {
        ClassLoader cloader = Thread.currentThread().getContextClassLoader();
        String appName = description.getAppName();

        CouchApp app = new CouchApp( appName, description );

        String appBase = APP_BASEPATH + description.getClasspathAppResource();

        String viewBase = appBase + VIEW_SUBPATH;

        Set<String> listing = description.getViewNames();
        if ( listing != null )
        {
            for ( String view : listing )
            {
                String mapPath = viewBase + view + MAP_JS;
                String reducePath = viewBase + view + REDUCE_JS;

                String map = null;

                InputStream in = cloader.getResourceAsStream( mapPath );
                if ( in == null )
                {
                    throw new CouchDBException(
                                                "Cannot read view: %s in CouchDB application: %s (classpath resource: %s)",
                                                view, appName, mapPath );
                }

                StringWriter sWriter = new StringWriter();
                try
                {
                    List<String> lines = readLines( in );
                    for ( String line : lines )
                    {
                        String test = line.trim();
                        if ( test.startsWith( "#" ) || test.startsWith( "/*" )
                            || test.startsWith( "*" ) || test.startsWith( "//" ) )
                        {
                            continue;
                        }
                        sWriter.write( line );
                        sWriter.write( '\n' );
                    }
                }
                finally
                {
                    closeQuietly( in );
                }
                map = sWriter.toString();

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
