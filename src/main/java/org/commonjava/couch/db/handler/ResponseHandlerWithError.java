package org.commonjava.couch.db.handler;

import org.apache.http.client.ResponseHandler;
import org.commonjava.couch.db.CouchDBException;

public interface ResponseHandlerWithError<T>
    extends ResponseHandler<T>
{

    CouchDBException getError();

}
