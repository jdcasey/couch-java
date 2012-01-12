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
package org.commonjava.web.common.ser;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.IOUtils;
import org.commonjava.util.logging.Logger;
import org.commonjava.web.common.model.Listing;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

@Singleton
public class JsonSerializer
{

    private final Logger logger = new Logger( getClass() );

    private final Set<WebSerializationAdapter> baseAdapters = new HashSet<WebSerializationAdapter>();

    @Inject
    @Any
    Instance<WebSerializationAdapter> adapterInstance;

    JsonSerializer()
    {
    }

    public JsonSerializer( final WebSerializationAdapter... baseAdapters )
    {
        this.baseAdapters.addAll( Arrays.asList( baseAdapters ) );
    }

    public void registerSerializationAdapters( final WebSerializationAdapter... adapters )
    {
        this.baseAdapters.addAll( Arrays.asList( adapters ) );
    }

    private Gson getGson()
    {
        final GsonBuilder builder = new GsonBuilder();
        if ( baseAdapters != null )
        {
            for ( final WebSerializationAdapter adapter : baseAdapters )
            {
                adapter.register( builder );
            }
        }

        if ( adapterInstance != null )
        {
            for ( final WebSerializationAdapter adapter : adapterInstance )
            {
                adapter.register( builder );
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
        catch ( final IOException e )
        {
            logger.error( "Failed to deserialize type: %s from HttpServletRequest body. Error: %s", e, type.getName(),
                          e.getMessage() );
            throw new RuntimeException( "Cannot read request." );
        }
    }

    public <T> T fromString( final String src, final Class<T> type,
                             final DeserializerPostProcessor<T>... postProcessors )
    {
        final T result = getGson().fromJson( src, type );

        if ( result != null )
        {
            for ( final DeserializerPostProcessor<T> proc : postProcessors )
            {
                proc.process( result );
            }
        }

        return result;
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
            final Reader reader = new InputStreamReader( stream, encoding );
            final String json = IOUtils.toString( reader );
            logger.info( "JSON:\n\n%s\n\n", json );

            final T result = getGson().fromJson( json, type );

            if ( result != null )
            {
                for ( final DeserializerPostProcessor<T> proc : postProcessors )
                {
                    proc.process( result );
                }
            }

            return result;
        }
        catch ( final UnsupportedEncodingException e )
        {
            logger.error( "Failed to deserialize type: %s. Error: %s", e, type.getName(), e.getMessage() );
            throw new RuntimeException( "Cannot read stream." );
        }
        catch ( final IOException e )
        {
            logger.error( "Failed to deserialize type: %s. Error: %s", e, type.getName(), e.getMessage() );
            throw new RuntimeException( "Cannot read stream." );
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
            Listing<T> result = getGson().fromJson( new InputStreamReader( stream, encoding ), token.getType() );

            if ( result != null && result.getItems() != null )
            {
                final List<T> items = result.getItems();
                Collections.reverse( items );

                result = new Listing<T>( items );
                for ( final T item : result.getItems() )
                {
                    for ( final DeserializerPostProcessor<T> proc : postProcessors )
                    {
                        proc.process( item );
                    }
                }
            }

            return result;
        }
        catch ( final UnsupportedEncodingException e )
        {
            logger.error( "Failed to deserialize type: %s. Error: %s", e, token.getType(), e.getMessage() );

            throw new RuntimeException( "Cannot read stream." );
        }
    }

    public <T> Listing<T> listingFromString( final String src, final TypeToken<Listing<T>> token,
                                             final DeserializerPostProcessor<T>... postProcessors )
    {
        Listing<T> result = getGson().fromJson( src, token.getType() );

        if ( result != null && result.getItems() != null )
        {
            final List<T> items = result.getItems();
            Collections.reverse( items );

            result = new Listing<T>( items );
            for ( final T item : result.getItems() )
            {
                for ( final DeserializerPostProcessor<T> proc : postProcessors )
                {
                    proc.process( item );
                }
            }
        }

        return result;
    }

}
