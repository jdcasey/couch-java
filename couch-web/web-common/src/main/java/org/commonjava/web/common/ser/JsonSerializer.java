package org.commonjava.web.common.ser;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.commonjava.util.logging.Logger;
import org.commonjava.web.common.model.Listing;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

public class JsonSerializer
{

    private final Logger logger = new Logger( getClass() );

    JsonSerializer()
    {}

    private Gson getGson()
    {
        GsonBuilder builder = new GsonBuilder();
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
