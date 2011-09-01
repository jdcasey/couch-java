/*******************************************************************************
 * Copyright (C) 2011  John Casey
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public
 * License along with this program.  If not, see 
 * <http://www.gnu.org/licenses/>.
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
