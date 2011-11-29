/*******************************************************************************
 * Copyright 2011 John Casey
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
