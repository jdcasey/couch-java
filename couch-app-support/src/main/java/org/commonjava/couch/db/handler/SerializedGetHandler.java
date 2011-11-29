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
package org.commonjava.couch.db.handler;

import static org.apache.commons.io.IOUtils.copy;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.log4j.Logger;
import org.commonjava.couch.db.CouchDBException;
import org.commonjava.couch.io.Serializer;
import org.commonjava.couch.io.json.SerializationAdapter;
import org.commonjava.couch.model.CouchError;

public class SerializedGetHandler<T>
    implements ResponseHandlerWithError<T>
{

    private static final Logger LOGGER = Logger.getLogger( SerializedGetHandler.class );

    private CouchDBException error;

    private final Serializer serializer;

    private final Type type;

    private final SerializationAdapter[] adapters;

    public SerializedGetHandler( final Serializer serializer, final Class<T> type )
    {
        this.serializer = serializer;
        this.type = type;
        this.adapters = new SerializationAdapter[] {};
    }

    public SerializedGetHandler( final Serializer serializer, final Type type,
                                 final SerializationAdapter... adapters )
    {
        this.serializer = serializer;
        this.type = type;
        this.adapters = adapters;
    }

    @Override
    public CouchDBException getError()
    {
        return error;
    }

    @Override
    public T handleResponse( final HttpResponse response )
        throws ClientProtocolException, IOException
    {
        // FIXME: Parse content-encoding??
        HttpEntity entity = response.getEntity();

        ByteArrayOutputStream out = null;
        InputStream in = null;
        try
        {
            if ( entity != null )
            {
                in = entity.getContent();
                out = new ByteArrayOutputStream();
                copy( in, out );

                if ( LOGGER.isDebugEnabled() )
                {
                    LOGGER.debug( new String( out.toByteArray() ) );
                }
            }

            StatusLine sl = response.getStatusLine();
            if ( sl.getStatusCode() != HttpStatus.SC_OK )
            {
                CouchError err = null;
                if ( out != null )
                {
                    LOGGER.info( "Body content: '" + new String( out.toByteArray() ) + "'" );
                    err =
                        serializer.toError( new ByteArrayInputStream( out.toByteArray() ), "UTF-8" );
                }

                error =
                    new CouchDBException( "Error returned from server: '%s'\nError message: %s",
                                          sl, err == null ? "-NONE-" : err );
            }
            else
            {
                return serializer.fromJson( new ByteArrayInputStream( out.toByteArray() ), "UTF-8",
                                            type, adapters );
            }
        }
        finally
        {
            in.close();
        }

        return null;
    }

}
