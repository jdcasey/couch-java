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
package org.commonjava.couch.change;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.commonjava.couch.io.Serializer;
import org.junit.Test;

public class CouchDocChangeDeserializerTest
{

    @Test
    public void deserializeNonDeletingChange()
    {
        String changeLine =
            "{\"seq\":714,\"id\":\"fa5d19967bf21b366ee6f45d54013928\",\"changes\":[{\"rev\":\"20-58b028db6d1b57b106f5f2610b89d192\"}]}";

        Object result =
            new Serializer().fromJson( changeLine, CouchDocChange.class,
                                       new CouchDocChangeDeserializer() );

        assertThat( result, notNullValue() );
        assertThat( ( result instanceof CouchDocChange ), equalTo( true ) );

        CouchDocChange change = (CouchDocChange) result;
        assertThat( change.getSequence(), equalTo( 714 ) );
        assertThat( change.getId(), equalTo( "fa5d19967bf21b366ee6f45d54013928" ) );
        assertThat( change.isDeleted(), equalTo( false ) );

        List<String> revs = change.getRevisions();
        assertThat( revs, notNullValue() );
        assertThat( revs.size(), equalTo( 1 ) );
        assertThat( revs.get( 0 ), equalTo( "20-58b028db6d1b57b106f5f2610b89d192" ) );
    }

    @Test
    public void deserializeDeletingChange()
    {
        String changeLine =
            "{\"seq\":783,\"id\":\"6aaa0ce24ff8196e4eaf7e3c14000a28\",\"changes\":[{\"rev\":\"3-7532023626276bbc71063b7958daf8f3\"}],\"deleted\":true}";

        Object result =
            new Serializer().fromJson( changeLine, CouchDocChange.class,
                                       new CouchDocChangeDeserializer() );

        assertThat( result, notNullValue() );
        assertThat( ( result instanceof CouchDocChange ), equalTo( true ) );

        CouchDocChange change = (CouchDocChange) result;
        assertThat( change.getSequence(), equalTo( 783 ) );
        assertThat( change.getId(), equalTo( "6aaa0ce24ff8196e4eaf7e3c14000a28" ) );
        assertThat( change.isDeleted(), equalTo( true ) );

        List<String> revs = change.getRevisions();
        assertThat( revs, notNullValue() );
        assertThat( revs.size(), equalTo( 1 ) );
        assertThat( revs.get( 0 ), equalTo( "3-7532023626276bbc71063b7958daf8f3" ) );
    }

}
