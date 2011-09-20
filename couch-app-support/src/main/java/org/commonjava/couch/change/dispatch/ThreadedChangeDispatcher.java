/*******************************************************************************
 * Copyright (C) 2011  John Casey
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public
 * License along with this program.  If not, see 
 * <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package org.commonjava.couch.change.dispatch;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executor;

import org.commonjava.couch.change.CouchDocChange;

public class ThreadedChangeDispatcher
    implements CouchChangeDispatcher
{

    private final Executor executor;

    private final List<? extends ThreadableListener> listeners;

    private final Set<ListenerRunnable> pendingDispatches = new LinkedHashSet<ListenerRunnable>();

    public ThreadedChangeDispatcher( final List<? extends ThreadableListener> listeners,
                                        final Executor executor )
    {
        this.listeners = listeners;
        this.executor = executor;
    }

    @Override
    public void documentChanged( final CouchDocChange change )
    {
        for ( ThreadableListener listener : listeners )
        {
            if ( listener.canProcess( change.getId(), change.isDeleted() ) )
            {
                ListenerRunnable run = new ListenerRunnable( listener, change );
                synchronized ( this )
                {
                    pendingDispatches.add( run );
                }

                executor.execute( run );
            }
        }
    }

    public int getPendingCount()
    {
        return pendingDispatches.size();
    }

    private final class ListenerRunnable
        implements Runnable
    {
        private final ThreadableListener listener;

        private final CouchDocChange change;

        ListenerRunnable( final ThreadableListener listener, final CouchDocChange change )
        {
            this.listener = listener;
            this.change = change;
        }

        @Override
        public void run()
        {
            listener.documentChanged( change );
            synchronized ( ThreadedChangeDispatcher.this )
            {
                pendingDispatches.remove( this );
                ThreadedChangeDispatcher.this.notifyAll();
            }
        }

    }

}
