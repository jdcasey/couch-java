/*******************************************************************************
 * Copyright 2011 John Casey
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package org.commonjava.couch.util;

public final class IdUtils
{

    private IdUtils()
    {}

    public static String nonNamespaceId( final String namespace, final String namespaceId )
    {
        if ( !namespaceId.startsWith( namespace ) )
        {
            return namespaceId;
        }

        return namespaceId.substring( namespace.length() + 1 );
    }

    public static String namespaceId( final String namespace, final String... parts )
    {
        StringBuilder sb = new StringBuilder();
        sb.append( namespace );
        for ( String part : parts )
        {
            sb.append( ":" ).append( part );
        }

        return sb.toString();
    }

}
