package org.commonjava.couch.util;

import static org.apache.commons.lang.StringUtils.join;

import java.util.Arrays;
import java.util.Collection;

public class JoinString
{

    private final Collection<?> items;

    private final String joint;

    public JoinString( final String joint, final Collection<?> items )
    {
        this.items = items;
        this.joint = joint;
    }

    public JoinString( final String joint, final Object... items )
    {
        this.items = Arrays.asList( items );
        this.joint = joint;
    }

    @Override
    public String toString()
    {
        return items == null || items.isEmpty() ? "-NONE-" : join( items, joint );
    }

}
