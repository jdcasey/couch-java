package org.commonjava.couch.db.action;

import java.util.concurrent.CountDownLatch;

import org.commonjava.couch.db.CouchDBException;
import org.commonjava.couch.db.CouchManager;
import org.commonjava.couch.model.CouchDocument;

public class StoreAction
    implements CouchDocumentAction
{

    private final CouchDocument document;

    private final boolean skipIfExists;

    private CouchManager manager;

    private String baseUrl;

    private CountDownLatch latch;

    private CouchDBException error;

    public StoreAction( final CouchDocument document, final boolean skipIfExists )
    {
        this.document = document;
        this.skipIfExists = skipIfExists;
    }

    @Override
    public CouchDocument getDocument()
    {
        return document;
    }

    @Override
    public void run()
    {
        try
        {
            manager.store( document, baseUrl, skipIfExists );
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
