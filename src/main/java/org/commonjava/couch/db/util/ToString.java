package org.commonjava.couch.db.util;


public class ToString
{

    private final Object[] params;

    private final String format;

    public ToString( final String format, final Object... params )
    {
        this.format = format;
        this.params = params;
    }

    @Override
    public String toString()
    {
        return String.format( format, params );
    }

}
