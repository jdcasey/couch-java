package org.commonjava.couch.change.dispatch;

import org.commonjava.couch.change.CouchDocChange;

public interface CouchChangeDispatcher
{

    void documentChanged( CouchDocChange change );

}
