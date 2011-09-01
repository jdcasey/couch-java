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
import org.commonjava.couch.model.CouchError;
import org.commonjava.couch.model.io.SerializationAdapter;
import org.commonjava.couch.model.io.Serializer;

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
