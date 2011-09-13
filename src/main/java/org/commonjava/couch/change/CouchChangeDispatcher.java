package org.commonjava.couch.change;

public interface CouchChangeDispatcher
{

    void documentChanged( CouchDocChange change );

}
