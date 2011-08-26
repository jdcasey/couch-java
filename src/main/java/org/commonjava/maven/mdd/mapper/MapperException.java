package org.commonjava.maven.mdd.mapper;

import org.commonjava.maven.mdd.MavenDependencyDBException;

public class MapperException
    extends MavenDependencyDBException
{

    private static final long serialVersionUID = 1L;

    public MapperException( final String message, final Throwable error, final Object... params )
    {
        super( message, error, params );
    }

    public MapperException( final String message, final Object... params )
    {
        super( message, params );
    }

}
