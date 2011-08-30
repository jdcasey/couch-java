package org.commonjava.couch.db.action;

import java.util.concurrent.CountDownLatch;

import org.commonjava.couch.db.CouchDBException;
import org.commonjava.couch.db.CouchManager;
import org.commonjava.couch.model.CouchDocument;

public interface CouchDocumentAction
    extends Runnable
{

    CouchDocument getDocument();

    CouchDBException getError();

    void prepareExecution( final CountDownLatch latch, final String baseUrl,
                           final CouchManager manager );

}
