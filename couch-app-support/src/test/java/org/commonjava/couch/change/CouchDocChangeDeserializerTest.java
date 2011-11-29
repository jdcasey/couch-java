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
