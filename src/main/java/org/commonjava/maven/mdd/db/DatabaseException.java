package org.commonjava.maven.mdd.db;

import java.util.List;

import org.commonjava.maven.mdd.MavenDependencyDBException;

public class DatabaseException
    extends MavenDependencyDBException
{

    private static final long serialVersionUID = 1L;

    private List<Throwable> nested;

    public DatabaseException( final String message, final Throwable error, final Object... params )
    {
        super( message, error, params );
    }

    public DatabaseException( final String message, final Object... params )
    {
        super( message, params );
    }

    public DatabaseException( final String message, final List<Throwable> nested,
                              final Object... params )
    {
        super( message, params );
        this.nested = nested;
    }

    @Override
    public String getMessage()
    {
        StringBuilder msg = new StringBuilder( super.getMessage() );
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
}
