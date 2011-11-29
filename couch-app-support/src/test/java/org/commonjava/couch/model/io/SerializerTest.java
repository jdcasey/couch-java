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
package org.commonjava.couch.model.io;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.commonjava.couch.db.action.BulkActionHolder;
import org.commonjava.couch.db.action.CouchDocumentAction;
import org.commonjava.couch.db.action.DeleteAction;
import org.commonjava.couch.db.action.StoreAction;
import org.commonjava.couch.db.model.CouchDocRefSet;
import org.commonjava.couch.db.model.CouchObjectList;
import org.commonjava.couch.db.model.SimpleAppDescription;
import org.commonjava.couch.fixture.TestUser;
import org.commonjava.couch.io.Serializer;
import org.commonjava.couch.io.json.CouchObjectListDeserializer;
import org.commonjava.couch.model.CouchApp;
import org.commonjava.couch.model.CouchAppView;
import org.commonjava.couch.model.CouchDocRef;
import org.commonjava.couch.model.CouchDocument;
import org.junit.Test;

import com.google.gson.annotations.SerializedName;

public class SerializerTest
{

    @Test
    public void serializeCouchDocRefSet()
    {
        final CouchDocRefSet set =
            new CouchDocRefSet( new CouchDocRef( "key1" ), new CouchDocRef( "key2" ), new CouchDocRef( "key3" ) );

        final String ser = new Serializer().toString( set );

        System.out.println( ser );
    }

    @Test
    public void deserializeObjectList()
    {
        final String src =
            "{\"total_rows\":1,\"offset\":0,\"rows\":[\n"
                + "{\"key\":\"username\",\"value\":\"2-abcdef012345678\",\"doc\":{\"username\":\"username\",\"first\":\"first\",\"last\":\"last\",\"email\":\"email@nowhere.com\",\"_id\":\"username\"}},\n"
                + "{\"key\":\"username2\",\"value\":\"3-bdefca34567216\",\"doc\":{\"username\":\"username2\",\"first\":\"first2\",\"last\":\"last2\",\"email\":\"email2@nowhere.com\",\"_id\":\"username2\"}}"
                + "]}";

        final CouchObjectListDeserializer<TestUser> deser =
            new CouchObjectListDeserializer<TestUser>( TestUser.class, false );

        final CouchObjectList<TestUser> listing = new Serializer().fromJson( src, deser.typeLiteral(), deser );

        assertThat( listing, notNullValue() );

        final List<TestUser> users = listing.getItems();

        assertThat( users, notNullValue() );
        assertThat( users.size(), equalTo( 2 ) );

        TestUser user = users.get( 0 );

        assertThat( user.getUsername(), equalTo( "username" ) );

        user = users.get( 1 );

        assertThat( user.getUsername(), equalTo( "username2" ) );
    }

    @Test
    public void serializeSimpleUser()
    {
        System.out.println( new Serializer().toString( new TestUser( "username", "first", "last", "email@nowhere.com" ) ) );
    }

    @Test
    public void serializeBulkStore()
    {
        final List<CouchDocumentAction> actions = new ArrayList<CouchDocumentAction>();
        actions.add( new StoreAction( new TestDoc( "foo", "bar", "bat" ), false ) );
        actions.add( new StoreAction( new TestDoc( "bar", "1-aabbeeff24422cc", "fieldname" ), false ) );

        System.out.println( new Serializer().toString( new BulkActionHolder( actions ) ) );
    }

    @Test
    public void serializeTransactionalBulkStore()
    {
        final List<CouchDocumentAction> actions = new ArrayList<CouchDocumentAction>();
        actions.add( new StoreAction( new TestDoc( "foo", "bar", "bat" ), false ) );
        actions.add( new StoreAction( new TestDoc( "bar", "1-aabbeeff24422cc", "fieldname" ), false ) );

        System.out.println( new Serializer().toString( new BulkActionHolder( actions, true ) ) );
    }

    @Test
    public void serializeBulkDelete()
    {
        final List<CouchDocumentAction> actions = new ArrayList<CouchDocumentAction>();
        actions.add( new DeleteAction( "foo" ) );
        actions.add( new DeleteAction( "bar", "1-aabbeeff24422cc" ) );

        System.out.println( new Serializer().toString( new BulkActionHolder( actions ) ) );
    }

    @Test
    public void serializeTransactionalBulkDelete()
    {
        final List<CouchDocumentAction> actions = new ArrayList<CouchDocumentAction>();
        actions.add( new DeleteAction( "foo" ) );
        actions.add( new DeleteAction( "bar", "1-aabbeeff24422cc" ) );

        System.out.println( new Serializer().toString( new BulkActionHolder( actions, true ) ) );
    }

    @Test
    public void serializeBulkModify()
    {
        final List<CouchDocumentAction> actions = new ArrayList<CouchDocumentAction>();
        actions.add( new StoreAction( new TestDoc( "foo", "bar", "bat" ), false ) );
        actions.add( new StoreAction( new TestDoc( "bar", "1-aabbeeff24422cc", "fieldname" ), false ) );
        actions.add( new DeleteAction( "foo" ) );
        actions.add( new DeleteAction( "bar", "1-aabbeeff24422cc" ) );

        System.out.println( new Serializer().toString( new BulkActionHolder( actions ) ) );
    }

    @Test
    public void serializeTransactionalBulkModify()
    {
        final List<CouchDocumentAction> actions = new ArrayList<CouchDocumentAction>();
        actions.add( new StoreAction( new TestDoc( "foo", "bar", "bat" ), false ) );
        actions.add( new StoreAction( new TestDoc( "bar", "1-aabbeeff24422cc", "fieldname" ), false ) );
        actions.add( new DeleteAction( "foo" ) );
        actions.add( new DeleteAction( "bar", "1-aabbeeff24422cc" ) );

        System.out.println( new Serializer().toString( new BulkActionHolder( actions, true ) ) );
    }

    @Test
    public void serializeApp()
    {
        final Map<String, CouchAppView> views = new HashMap<String, CouchAppView>();
        views.put( "test-view", new CouchAppView( "function(doc){emit(doc._id,doc._rev );}" ) );

        System.out.println( new Serializer().toString( new CouchApp( "test-app", views,
                                                                     new SimpleAppDescription( "test-app", "test-app",
                                                                                               "test-view" ) ) ) );
    }

    public class TestDoc
        implements CouchDocument
    {

        @SerializedName( "_id" )
        private String id;

        @SerializedName( "_rev" )
        private String rev;

        private String field;

        TestDoc( final String id, final String rev, final String field )
        {
            this.id = id;
            this.rev = rev;
            this.field = field;
        }

        @Override
        public String getCouchDocId()
        {
            return id;
        }

        @Override
        public String getCouchDocRev()
        {
            return rev;
        }

        String getField()
        {
            return field;
        }

        void setId( final String id )
        {
            this.id = id;
        }

        void setRev( final String rev )
        {
            this.rev = rev;
        }

        void setField( final String field )
        {
            this.field = field;
        }

        @Override
        public void setCouchDocRev( final String revision )
        {
            this.rev = revision;
        }

    }

}
