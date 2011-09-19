package org.commonjava.couch.change.dispatch;

import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.commonjava.couch.change.CouchDocChange;

@Singleton
public class J2EEChangeDispatcher
    implements CouchChangeDispatcher
{

    @Inject
    private Event<CouchChangeJ2EEEvent> event;

    @Override
    public void documentChanged( final CouchDocChange change )
    {
        event.fire( new CouchChangeJ2EEEvent( change ) );
    }

}
