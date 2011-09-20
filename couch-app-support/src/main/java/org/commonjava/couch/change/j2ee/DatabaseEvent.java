package org.commonjava.couch.change.j2ee;

public class DatabaseEvent
{
    public enum Type
    {
        CREATE, DROP;
    }

    private final Type type;

    private final String url;

    public DatabaseEvent( final Type type, final String url )
    {
        this.type = type;
        this.url = url;
    }

    public Type getType()
    {
        return type;
    }

    public String getUrl()
    {
        return url;
    }
}
