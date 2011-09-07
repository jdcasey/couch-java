package org.commonjava.web.common.ser;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.commonjava.util.logging.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class RestSerializer
{

    private final Logger logger = new Logger( getClass() );

    RestSerializer()
    {}

    private Gson getGson()
    {
        GsonBuilder builder = new GsonBuilder();
        return builder.create();
    }

    public String toJson( final Object src )
    {
        return getGson().toJson( src );
    }

    public String toJson( final Object src, final Type type )
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
            T result =
                getGson().fromJson( new InputStreamReader( req.getInputStream(), encoding ), type );

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
            logger.error( "Failed to deserialize type: %s from HttpServletRequest body. Error: %s",
                          e, type.getName(), e.getMessage() );
            throw new WebApplicationException(
                                               Response.status( Status.INTERNAL_SERVER_ERROR ).build() );
        }
        catch ( IOException e )
        {
            logger.error( "Failed to deserialize type: %s from HttpServletRequest body. Error: %s",
                          e, type.getName(), e.getMessage() );
            throw new WebApplicationException(
                                               Response.status( Status.INTERNAL_SERVER_ERROR ).build() );
        }
    }

}
