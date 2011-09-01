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
