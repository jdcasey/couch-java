package org.commonjava.maven.mdd.model;

import org.commonjava.maven.mdd.MavenDependencyDBException;

public class InvalidKeyException
    extends MavenDependencyDBException
{

    private static final long serialVersionUID = 1L;

    public InvalidKeyException( final String message, final Object... params )
    {
        super( message, params );
    }

    public InvalidKeyException( final String message, final Throwable error, final Object... params )
    {
        super( message, error, params );
    }

}
