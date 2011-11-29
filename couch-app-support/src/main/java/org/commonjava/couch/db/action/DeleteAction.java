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
import org.commonjava.couch.model.AbstractCouchDocument;
import org.commonjava.couch.model.CouchDocument;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DeleteAction
    implements CouchDocumentAction
{

    private final CouchDocument document;

    private CouchDBException error;

    private CountDownLatch latch;

    private CouchManager manager;

    public DeleteAction( final CouchDocument document )
    {
        this.document = new DeleteDoc( document );
    }

    public DeleteAction( final String key, final String revision )
    {
        this.document = new DeleteDoc( key, revision );
    }

    public DeleteAction( final String key )
    {
        this.document = new DeleteDoc( key );
    }

    @Override
    public CouchDocument getDocument()
    {
        return document;
    }

    static final class DeleteDoc
        extends AbstractCouchDocument
    {
        @SuppressWarnings( "unused" )
        @SerializedName( "_deleted" )
        @Expose( deserialize = false )
        private final boolean delete = true;

        DeleteDoc()
        {}

        DeleteDoc( final CouchDocument doc )
        {
            setCouchDocId( doc.getCouchDocId() );
            setCouchDocRev( doc.getCouchDocRev() );
        }

        DeleteDoc( final String id, final String rev )
        {
            setCouchDocId( id );
            setCouchDocRev( rev );
        }

        DeleteDoc( final String id )
        {
            setCouchDocId( id );
        }

    }

    @Override
    public void run()
    {
        try
        {
            manager.delete( document );
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
