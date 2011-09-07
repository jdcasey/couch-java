package org.commonjava.web.common.ser;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.commonjava.couch.model.DenormalizationException;
import org.commonjava.couch.model.DenormalizedCouchDoc;
import org.commonjava.util.logging.Logger;

public class DenormalizerPostProcessor<T extends DenormalizedCouchDoc>
    implements DeserializerPostProcessor<T>
{

    private final Logger logger = new Logger( getClass() );

    @Override
    public void process( final T value )
    {
        try
        {
            value.calculateDenormalizedFields();
        }
        catch ( DenormalizationException e )
        {
            logger.error( "Failed to denormalize values for: %s. Reason: %s", e, value,
                          e.getMessage() );

            throw new WebApplicationException( Response.status( Status.BAD_REQUEST ).build() );
        }
    }

}
