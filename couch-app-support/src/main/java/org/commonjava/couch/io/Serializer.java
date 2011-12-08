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
package org.commonjava.couch.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.inject.Singleton;

import org.apache.http.HttpEntity;
import org.commonjava.couch.db.action.BulkActionHolder;
import org.commonjava.couch.db.action.CouchDocumentAction;
import org.commonjava.couch.db.model.CouchDocRefSet;
import org.commonjava.couch.io.json.CouchDocumentActionAdapter;
import org.commonjava.couch.model.CouchDocument;
import org.commonjava.couch.model.CouchError;
import org.commonjava.web.common.ser.WebSerializationAdapter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@Singleton
public class Serializer
{

    private final Set<WebSerializationAdapter> baseAdapters = new HashSet<WebSerializationAdapter>();

    Serializer()
    {
    }

    public Serializer( final WebSerializationAdapter... baseAdapters )
    {
        this.baseAdapters.addAll( Arrays.asList( baseAdapters ) );
    }

    public void registerSerializationAdapters( final WebSerializationAdapter... adapters )
    {
        this.baseAdapters.addAll( Arrays.asList( adapters ) );
    }

    public String toString( final BulkActionHolder actions, final WebSerializationAdapter... adapters )
    {
        return getGson( adapters ).toJson( actions );
    }

    public String toString( final CouchDocRefSet refSet, final WebSerializationAdapter... adapters )
    {
        return getGson( adapters ).toJson( refSet );
    }

    public String toString( final CouchDocument doc, final WebSerializationAdapter... adapters )
    {
        return getGson( adapters ).toJson( doc );
    }

    public <D extends CouchDocument> D toDocument( final String src, final Class<D> docType,
                                                   final WebSerializationAdapter... adapters )
    {
        return getGson( adapters ).fromJson( src, docType );
    }

    public <D extends CouchDocument> D toDocument( final InputStream src, final String encoding,
                                                   final Class<D> docType, final WebSerializationAdapter... adapters )
        throws UnsupportedEncodingException
    {
        return getGson( adapters ).fromJson( new InputStreamReader( src, encoding ), docType );
    }

    public <T> T fromJson( final String src, final Type type, final WebSerializationAdapter... adapters )
    {
        return getGson( adapters ).fromJson( src, type );
    }

    public <T> T fromJson( final InputStream src, final String encoding, final Type type,
                           final WebSerializationAdapter... adapters )
        throws UnsupportedEncodingException
    {
        return getGson( adapters ).fromJson( new InputStreamReader( src, encoding ), type );
    }

    public CouchError toError( final InputStream in, final String charset )
        throws UnsupportedEncodingException
    {
        final Reader reader = new InputStreamReader( in, charset );
        return getGson().fromJson( reader, CouchError.class );
    }

    public CouchError toError( final HttpEntity entity )
        throws IOException
    {
        if ( entity == null )
        {
            return null;
        }

        if ( entity.getContentEncoding() != null )
        {
            System.out.printf( "Content-Encoding header: '%s' = '%s'\n", entity.getContentEncoding()
                                                                               .getName(), entity.getContentEncoding()
                                                                                                 .getValue() );
        }

        final Reader reader = new InputStreamReader( entity.getContent() );
        return getGson().fromJson( reader, CouchError.class );
    }

    protected GsonBuilder newGsonBuilder()
    {
        final GsonBuilder builder = new GsonBuilder();
        // builder.setPrettyPrinting();
        builder.registerTypeAdapter( CouchDocumentAction.class, new CouchDocumentActionAdapter() );

        return builder;
    }

    protected final Gson getGson( final WebSerializationAdapter... adapters )
    {
        final GsonBuilder builder = newGsonBuilder();
        if ( baseAdapters != null )
        {
            for ( final WebSerializationAdapter adapter : baseAdapters )
            {
                builder.registerTypeAdapter( adapter.typeLiteral(), adapter );
            }
        }

        if ( adapters != null )
        {
            for ( final WebSerializationAdapter adapter : adapters )
            {
                builder.registerTypeAdapter( adapter.typeLiteral(), adapter );
            }
        }

        return builder.create();
    }

}
