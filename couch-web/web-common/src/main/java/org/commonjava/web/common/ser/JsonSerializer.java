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
package org.commonjava.web.common.ser;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Singleton;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.commonjava.couch.io.json.SerializationAdapter;
import org.commonjava.util.logging.Logger;
import org.commonjava.web.common.model.Listing;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

@Singleton
public class JsonSerializer
{

    private final Logger logger = new Logger( getClass() );

    private final Set<SerializationAdapter> baseAdapters = new HashSet<SerializationAdapter>();

    JsonSerializer()
    {}

    public JsonSerializer( final SerializationAdapter... baseAdapters )
    {
        this.baseAdapters.addAll( Arrays.asList( baseAdapters ) );
    }

    public void registerSerializationAdapters( final SerializationAdapter... adapters )
    {
        this.baseAdapters.addAll( Arrays.asList( adapters ) );
    }

    private Gson getGson()
    {
        GsonBuilder builder = new GsonBuilder();
        if ( baseAdapters != null )
        {
            for ( SerializationAdapter adapter : baseAdapters )
            {
                builder.registerTypeAdapter( adapter.typeLiteral(), adapter );
            }
        }
        return builder.create();
    }

    public String toString( final Object src )
    {
        return getGson().toJson( src );
    }

    public String toString( final Object src, final Type type )
    {
        return getGson().toJson( src, type );
    }

    public <T> T fromRequestBody( final HttpServletRequest req, final Class<T> type,
                                  final DeserializerPostProcessor<T>... postProcessors )
    {
        String encoding = req.getCharacterEncoding();
        if ( encoding == null )
        {
            encoding = "UTF-8";
        }

        try
        {
            return fromStream( req.getInputStream(), encoding, type, postProcessors );
        }
        catch ( IOException e )
        {
            logger.error( "Failed to deserialize type: %s from HttpServletRequest body. Error: %s",
                          e, type.getName(), e.getMessage() );
            throw new WebApplicationException(
                                               Response.status( Status.INTERNAL_SERVER_ERROR ).build() );
        }
    }

    public <T> T fromStream( final InputStream stream, String encoding, final Class<T> type,
                             final DeserializerPostProcessor<T>... postProcessors )
    {
        if ( encoding == null )
        {
            encoding = "UTF-8";
        }

        try
        {
            T result = getGson().fromJson( new InputStreamReader( stream, encoding ), type );

            if ( result != null )
            {
                for ( DeserializerPostProcessor<T> proc : postProcessors )
                {
                    proc.process( result );
                }
            }

            return result;
        }
        catch ( UnsupportedEncodingException e )
        {
            logger.error( "Failed to deserialize type: %s. Error: %s", e, type.getName(),
                          e.getMessage() );
            throw new WebApplicationException(
                                               Response.status( Status.INTERNAL_SERVER_ERROR ).build() );
        }
    }

    public <T> Listing<T> listingFromStream( final InputStream stream, String encoding,
                                             final TypeToken<Listing<T>> token,
                                             final DeserializerPostProcessor<T>... postProcessors )
    {
        if ( encoding == null )
        {
            encoding = "UTF-8";
        }

        try
        {
            Listing<T> result =
                getGson().fromJson( new InputStreamReader( stream, encoding ), token.getType() );

            if ( result != null && result.getItems() != null )
            {
                List<T> items = result.getItems();
                Collections.reverse( items );

                result = new Listing<T>( items );
                for ( T item : result )
                {
                    for ( DeserializerPostProcessor<T> proc : postProcessors )
                    {
                        proc.process( item );
                    }
                }
            }

            return result;
        }
        catch ( UnsupportedEncodingException e )
        {
            logger.error( "Failed to deserialize type: %s. Error: %s", e, token.getType(),
                          e.getMessage() );

            throw new WebApplicationException(
                                               Response.status( Status.INTERNAL_SERVER_ERROR ).build() );
        }
    }

}
