package org.commonjava.web.common.model;

import static org.apache.commons.lang.StringUtils.join;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class MappingArray
    implements Iterable<String>
{

    private String[] elements;

    public String[] getElements()
    {
        return elements;
    }

    public void setElements( final String[] elements )
    {
        this.elements = elements;
    }

    @Override
    public Iterator<String> iterator()
    {
        List<String> eltList;
        if ( elements == null )
        {
            eltList = Collections.emptyList();
        }
        else
        {
            eltList = Arrays.asList( elements );
        }

        return eltList.iterator();
    }

    @Override
    public String toString()
    {
        return elements == null ? "-NONE-" : join( elements, ", " );
    }

}
