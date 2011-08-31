package org.commonjava.auth.couch.util;

public final class IdUtils
{

    private IdUtils()
    {}

    public static String namespaceId( final String namespace, final String id )
    {
        return namespace + ":" + id;
    }

}
