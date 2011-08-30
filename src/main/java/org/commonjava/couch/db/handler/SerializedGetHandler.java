package org.commonjava.couch.db.handler;

import static org.apache.commons.io.IOUtils.copy;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.log4j.Logger;
import org.commonjava.couch.db.CouchDBException;
import org.commonjava.couch.model.CouchError;
import org.commonjava.couch.model.io.Serializer;

public class SerializedGetHandler<T>
    implements ResponseHandlerWithError<T>
{

    private static final Logger LOGGER = Logger.getLogger( SerializedGetHandler.class );

    private CouchDBException error;

    private final Serializer serializer;

    private final Class<T> type;

    public SerializedGetHandler( final Serializer serializer, final Class<T> type )
    {
        this.serializer = serializer;
        this.type = type;
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
                                            type );
            }
        }
        finally
        {
            in.close();
        }

        return null;
    }

}
