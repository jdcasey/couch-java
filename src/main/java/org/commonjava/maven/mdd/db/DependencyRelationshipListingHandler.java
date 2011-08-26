package org.commonjava.maven.mdd.db;

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
import org.apache.http.client.ResponseHandler;
import org.apache.log4j.Logger;
import org.commonjava.maven.mdd.model.DatabaseError;
import org.commonjava.maven.mdd.model.DependencyRelationshipListing;
import org.commonjava.maven.mdd.model.io.Serializer;

public class DependencyRelationshipListingHandler
    implements ResponseHandler<DependencyRelationshipListing>
{

    private static final Logger LOGGER =
        Logger.getLogger( DependencyRelationshipListingHandler.class );

    private DatabaseException error;

    private final Serializer serializer;

    public DependencyRelationshipListingHandler( final Serializer serializer )
    {
        this.serializer = serializer;
    }

    public DatabaseException getError()
    {
        return error;
    }

    @Override
    public DependencyRelationshipListing handleResponse( final HttpResponse response )
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
                DatabaseError err = null;
                if ( out != null )
                {
                    err =
                        serializer.toError( new ByteArrayInputStream( out.toByteArray() ), "UTF-8" );
                }

                error =
                    new DatabaseException( "Error returned from server: '%s'\nError message: %s",
                                           sl, err == null ? "-NONE-" : err );
            }
            else
            {
                return serializer.toDependencyListing( new ByteArrayInputStream( out.toByteArray() ),
                                                       "UTF-8" );
            }
        }
        finally
        {
            in.close();
        }

        return null;
    }

}
