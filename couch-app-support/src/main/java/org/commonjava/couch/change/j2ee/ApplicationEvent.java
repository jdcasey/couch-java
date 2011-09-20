package org.commonjava.couch.change.j2ee;

import org.commonjava.couch.db.model.AppDescription;

public class ApplicationEvent
{

    public enum Type
    {
        INSTALL;
    }

    private final Type type;

    private final AppDescription description;

    public ApplicationEvent( final Type type, final AppDescription description )
    {
        this.type = type;
        this.description = description;
    }

    public Type getType()
    {
        return type;
    }

    public AppDescription getDescription()
    {
        return description;
    }

}
