package org.commonjava.couch.util;

public final class UrlUtils
{

    private UrlUtils()
    {}

    public static String stringQueryParameter( final Object value )
    {
        String base = String.valueOf( value );
        return "%22" + base + "%22";
    }

}
