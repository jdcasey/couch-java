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
package org.commonjava.couch.model.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;

import org.apache.http.HttpEntity;
import org.commonjava.couch.db.action.BulkActionHolder;
import org.commonjava.couch.db.action.CouchDocumentAction;
import org.commonjava.couch.model.CouchDocument;
import org.commonjava.couch.model.CouchError;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Serializer
{

    private final SerializationAdapter[] baseAdapters;

    public Serializer( final SerializationAdapter... baseAdapters )
    {
        this.baseAdapters = baseAdapters;
    }

    public String toString( final BulkActionHolder actions, final SerializationAdapter... adapters )
    {
        return getGson( adapters ).toJson( actions );
    }

    public String toString( final CouchDocument doc, final SerializationAdapter... adapters )
    {
        return getGson( adapters ).toJson( doc );
    }

    public <D extends CouchDocument> D toDocument( final String src, final Class<D> docType,
                                                   final SerializationAdapter... adapters )
    {
        return getGson( adapters ).fromJson( src, docType );
    }

    public <D extends CouchDocument> D toDocument( final InputStream src, final String encoding,
                                                   final Class<D> docType,
                                                   final SerializationAdapter... adapters )
        throws UnsupportedEncodingException
    {
        return getGson( adapters ).fromJson( new InputStreamReader( src, encoding ), docType );
    }

    public <T> T fromJson( final String src, final Type type,
                           final SerializationAdapter... adapters )
    {
        return getGson( adapters ).fromJson( src, type );
    }

    public <T> T fromJson( final InputStream src, final String encoding, final Type type,
                           final SerializationAdapter... adapters )
        throws UnsupportedEncodingException
    {
        return getGson( adapters ).fromJson( new InputStreamReader( src, encoding ), type );
    }

    public CouchError toError( final InputStream in, final String charset )
        throws UnsupportedEncodingException
    {
        Reader reader = new InputStreamReader( in, charset );
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
            System.out.printf( "Content-Encoding header: '%s' = '%s'\n",
                               entity.getContentEncoding().getName(),
                               entity.getContentEncoding().getValue() );
        }

        Reader reader = new InputStreamReader( entity.getContent() );
        return getGson().fromJson( reader, CouchError.class );
    }

    protected GsonBuilder newGsonBuilder()
    {
        GsonBuilder builder = new GsonBuilder();
        // builder.setPrettyPrinting();
        builder.registerTypeAdapter( CouchDocumentAction.class, new CouchDocumentActionAdapter() );

        return builder;
    }

    protected final Gson getGson( final SerializationAdapter... adapters )
    {
        GsonBuilder builder = newGsonBuilder();
        if ( baseAdapters != null )
        {
            for ( SerializationAdapter adapter : baseAdapters )
            {
                builder.registerTypeAdapter( adapter.typeLiteral(), adapter );
            }
        }

        if ( adapters != null )
        {
            for ( SerializationAdapter adapter : adapters )
            {
                builder.registerTypeAdapter( adapter.typeLiteral(), adapter );
            }
        }

        return builder.create();
    }

}
