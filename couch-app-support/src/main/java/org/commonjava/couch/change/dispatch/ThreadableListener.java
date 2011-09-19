package org.commonjava.couch.change.dispatch;

import org.commonjava.couch.change.CouchDocChange;

public interface ThreadableListener
{
    boolean canProcess( String id, boolean deleted );

    void documentChanged( CouchDocChange change );

    void waitForChange( long totalMillis, long pollingMillis );
}