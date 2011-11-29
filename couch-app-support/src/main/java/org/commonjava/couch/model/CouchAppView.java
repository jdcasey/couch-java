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
package org.commonjava.couch.model;

public class CouchAppView
{
    private String map;

    private String reduce;

    CouchAppView()
    {}

    public CouchAppView( final String map, final String reduce )
    {
        this.map = map;
        this.reduce = reduce;
    }

    public CouchAppView( final String map )
    {
        this.map = map;
    }

    public String getMap()
    {
        return map;
    }

    void setMap( final String map )
    {
        this.map = map;
    }

    public String getReduce()
    {
        return reduce;
    }

    void setReduce( final String reduce )
    {
        this.reduce = reduce;
    }
}
