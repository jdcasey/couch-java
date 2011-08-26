package org.commonjava.poc.mdb.db;

import java.text.MessageFormat;
import java.util.IllegalFormatException;

public class DatabaseException
    extends Exception
{

    private static final long serialVersionUID = 1L;

    private final Object[] params;

    public DatabaseException( final String message, final Throwable error, final Object... params )
    {
        super( message, error );
        this.params = params;
    }

    public DatabaseException( final String message, final Object... params )
    {
        super( message );
        this.params = params;
    }

    @Override
    public String getLocalizedMessage()
    {
        return getMessage();
    }

    @Override
    public String getMessage()
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

}
