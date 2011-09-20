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
