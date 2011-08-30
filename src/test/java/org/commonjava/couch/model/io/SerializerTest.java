package org.commonjava.couch.model.io;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.commonjava.couch.db.action.BulkActionHolder;
import org.commonjava.couch.db.action.CouchDocumentAction;
import org.commonjava.couch.db.action.DeleteAction;
import org.commonjava.couch.db.action.StoreAction;
import org.commonjava.couch.model.CouchApp;
import org.commonjava.couch.model.CouchAppView;
import org.commonjava.couch.model.CouchDocument;
import org.junit.Test;

import com.google.gson.annotations.SerializedName;

public class SerializerTest
{

    @Test
    public void serializeBulkStore()
    {
        List<CouchDocumentAction> actions = new ArrayList<CouchDocumentAction>();
        actions.add( new StoreAction( new TestDoc( "foo", "bar", "bat" ), false ) );
        actions.add( new StoreAction( new TestDoc( "bar", "1-aabbeeff24422cc", "fieldname" ), false ) );

        System.out.println( new Serializer().toString( new BulkActionHolder( actions ) ) );
    }

    @Test
    public void serializeTransactionalBulkStore()
    {
        List<CouchDocumentAction> actions = new ArrayList<CouchDocumentAction>();
        actions.add( new StoreAction( new TestDoc( "foo", "bar", "bat" ), false ) );
        actions.add( new StoreAction( new TestDoc( "bar", "1-aabbeeff24422cc", "fieldname" ), false ) );

        System.out.println( new Serializer().toString( new BulkActionHolder( actions, true ) ) );
    }

    @Test
    public void serializeBulkDelete()
    {
        List<CouchDocumentAction> actions = new ArrayList<CouchDocumentAction>();
        actions.add( new DeleteAction( "foo" ) );
        actions.add( new DeleteAction( "bar", "1-aabbeeff24422cc" ) );

        System.out.println( new Serializer().toString( new BulkActionHolder( actions ) ) );
    }

    @Test
    public void serializeTransactionalBulkDelete()
    {
        List<CouchDocumentAction> actions = new ArrayList<CouchDocumentAction>();
        actions.add( new DeleteAction( "foo" ) );
        actions.add( new DeleteAction( "bar", "1-aabbeeff24422cc" ) );

        System.out.println( new Serializer().toString( new BulkActionHolder( actions, true ) ) );
    }

    @Test
    public void serializeBulkModify()
    {
        List<CouchDocumentAction> actions = new ArrayList<CouchDocumentAction>();
        actions.add( new StoreAction( new TestDoc( "foo", "bar", "bat" ), false ) );
        actions.add( new StoreAction( new TestDoc( "bar", "1-aabbeeff24422cc", "fieldname" ), false ) );
        actions.add( new DeleteAction( "foo" ) );
        actions.add( new DeleteAction( "bar", "1-aabbeeff24422cc" ) );

        System.out.println( new Serializer().toString( new BulkActionHolder( actions ) ) );
    }

    @Test
    public void serializeTransactionalBulkModify()
    {
        List<CouchDocumentAction> actions = new ArrayList<CouchDocumentAction>();
        actions.add( new StoreAction( new TestDoc( "foo", "bar", "bat" ), false ) );
        actions.add( new StoreAction( new TestDoc( "bar", "1-aabbeeff24422cc", "fieldname" ), false ) );
        actions.add( new DeleteAction( "foo" ) );
        actions.add( new DeleteAction( "bar", "1-aabbeeff24422cc" ) );

        System.out.println( new Serializer().toString( new BulkActionHolder( actions, true ) ) );
    }

    @Test
    public void serializeApp()
    {
        Map<String, CouchAppView> views = new HashMap<String, CouchAppView>();
        views.put( "test-view", new CouchAppView( "function(doc){emit(doc._id,doc._rev );}" ) );

        System.out.println( new Serializer().toString( new CouchApp( "test-app", views ) ) );
    }

    @Test
    public void deserializeApp()
    {
        String src =
            "{\"_id\":\"_design/test-app\",\"language\":\"javascript\",\"views\":{\"test-view\":{\n"
                + "      \"map\":\"function(doc){emit(doc._id,doc._rev );}\"}}}";

        CouchApp app = new Serializer().toDocument( src, CouchApp.class );

        System.out.println( app );
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
