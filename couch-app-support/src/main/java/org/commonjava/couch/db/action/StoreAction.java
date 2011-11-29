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
            manager.store( document, skipIfExists );
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
    public void prepareExecution( final CountDownLatch latch, final CouchManager manager )
    {
        this.manager = manager;
        this.latch = latch;
    }

}
