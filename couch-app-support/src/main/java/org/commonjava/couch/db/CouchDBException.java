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
package org.commonjava.couch.db;

import java.text.MessageFormat;
import java.util.IllegalFormatException;
import java.util.List;

public class CouchDBException
    extends Exception
{

    private static final long serialVersionUID = 1L;

    private List<? extends Throwable> nested;

    private final Object[] params;

    public CouchDBException( final String message, final Throwable error, final Object... params )
    {
        super( message, error );
        this.params = params;
    }

    public CouchDBException( final String message, final Object... params )
    {
        super( message );
        this.params = params;
    }

    @Override
    public String getMessage()
    {
        StringBuilder msg = new StringBuilder( formatMessage() );
        if ( nested != null && !nested.isEmpty() )
        {
            msg.append( "\nNested errors:\n" );

            int idx = 1;
            for ( Throwable error : nested )
            {
                msg.append( "\n" ).append( idx ).append( ".  " ).append( error.getMessage() );
                idx++;
            }
        }

        return msg.toString();
    }

    @Override
    public String getLocalizedMessage()
    {
        return getMessage();
    }

    public String formatMessage()
    {
        String message = super.getMessage();

        if ( params != null )
        {
            try
            {
                message = String.format( message, params );
            }
            catch ( IllegalFormatException ife )
            {
                try
                {
                    message = MessageFormat.format( message, params );
                }
                catch ( IllegalArgumentException iae )
                {}
            }
        }

        return message;
    }

    public CouchDBException withNestedErrors( final List<? extends Throwable> errors )
    {
        this.nested = errors;
        return this;
    }
}
