package org.commonjava.couch.db.action;

import java.util.concurrent.CountDownLatch;

import org.commonjava.couch.db.CouchDBException;
import org.commonjava.couch.db.CouchManager;
import org.commonjava.couch.model.AbstractCouchDocument;
import org.commonjava.couch.model.CouchDocument;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DeleteAction
    implements CouchDocumentAction
{

    private final CouchDocument document;

    private CouchDBException error;

    private CountDownLatch latch;

    private String baseUrl;

    private CouchManager manager;

    public DeleteAction( final CouchDocument document )
    {
        this.document = new DeleteDoc( document );
    }

    public DeleteAction( final String key, final String revision )
    {
        this.document = new DeleteDoc( key, revision );
    }

    public DeleteAction( final String key )
    {
        this.document = new DeleteDoc( key );
    }

    @Override
    public CouchDocument getDocument()
    {
        return document;
    }

    static final class DeleteDoc
        extends AbstractCouchDocument
    {
        @SuppressWarnings( "unused" )
        @SerializedName( "_deleted" )
        @Expose( deserialize = false )
        private final boolean delete = true;

        DeleteDoc()
        {}

        DeleteDoc( final CouchDocument doc )
        {
            setCouchDocId( doc.getCouchDocId() );
            setCouchDocRev( doc.getCouchDocRev() );
        }

        DeleteDoc( final String id, final String rev )
        {
            setCouchDocId( id );
            setCouchDocRev( rev );
        }

        DeleteDoc( final String id )
        {
            setCouchDocId( id );
        }

    }

    @Override
    public void run()
    {
        try
        {
            manager.delete( document, baseUrl );
        }
        catch ( CouchDBException e )
        {
            error = e;
        }
        finally
        {
            latch.countDown();
        }
    }

    @Override
    public CouchDBException getError()
    {
        return error;
    }

    @Override
    public void prepareExecution( final CountDownLatch latch, final String baseUrl,
                                  final CouchManager manager )
    {
        this.baseUrl = baseUrl;
        this.manager = manager;
        this.latch = latch;
    }

}
