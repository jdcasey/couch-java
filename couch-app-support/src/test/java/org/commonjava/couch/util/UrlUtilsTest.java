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

import static org.commonjava.couch.util.UrlUtils.siblingDatabaseUrl;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class UrlUtilsTest
{

    @Test
    public void buildSiblingDatabaseUrl()
    {
        final String baseUrl = "http://localhost:5984/";
        final String defDb = baseUrl + "default-db";
        final String sibName = "sibling-db";
        final String sibDb = siblingDatabaseUrl( defDb, sibName );

        assertThat( sibDb, equalTo( baseUrl + sibName ) );
    }

    @Test
    public void buildSiblingDatabaseUrl_SourceHasTerminatingPathSeparator()
    {
        final String baseUrl = "http://localhost:5984/";
        final String defDb = baseUrl + "default-db/";
        final String sibName = "sibling-db";
        final String sibDb = siblingDatabaseUrl( defDb, sibName );

        assertThat( sibDb, equalTo( baseUrl + sibName ) );
    }

    @Test( expected = IllegalArgumentException.class )
    public void buildSiblingDatabaseUrl_SourceHasNoPath()
    {
        siblingDatabaseUrl( "http://localhost:5984", "sibling-db" );
    }

    @Test( expected = IllegalArgumentException.class )
    public void buildSiblingDatabaseUrl_SourceIsEmpty()
    {
        siblingDatabaseUrl( "", "sibling-db" );
    }

    @Test( expected = IllegalArgumentException.class )
    public void buildSiblingDatabaseUrl_SourceIsNull()
    {
        siblingDatabaseUrl( null, "sibling-db" );
    }

}
