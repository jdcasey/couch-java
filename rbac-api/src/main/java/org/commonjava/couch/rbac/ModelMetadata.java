package org.commonjava.couch.rbac;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ModelMetadata
{

    private final Map<String, Object> metadata = new HashMap<String, Object>();

    public void setMetadata( final String key, final Object value )
    {
        metadata.put( key, value );
    }

    public <T> T getMetadata( final String key, final Class<T> type )
    {
        final Object val = metadata.get( key );
        if ( val != null )
        {
            return type.cast( val );
        }

        return null;
    }

    public Object getRawMetadata( final String key )
    {
        return metadata.get( key );
    }

    public Set<String> getMetadataKeys()
    {
        return Collections.unmodifiableSet( metadata.keySet() );
    }

}
