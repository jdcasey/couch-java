package org.commonjava.couch.model.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpEntity;
import org.codehaus.plexus.component.annotations.Component;
import org.commonjava.couch.db.action.BulkActionHolder;
import org.commonjava.couch.db.action.CouchDocumentAction;
import org.commonjava.couch.model.CouchDocument;
import org.commonjava.couch.model.CouchError;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@Component( role = Serializer.class )
public class Serializer
{

    public String toString( final BulkActionHolder actions )
    {
        return getGson().toJson( actions );
    }

    public String toString( final CouchDocument doc )
    {
        return getGson().toJson( doc );
    }

    public <D extends CouchDocument> D toDocument( final String src, final Class<D> docType )
    {
        return getGson().fromJson( src, docType );
    }

    public <D extends CouchDocument> D toDocument( final InputStream src, final String encoding,
                                                   final Class<D> docType )
        throws UnsupportedEncodingException
    {
        return getGson().fromJson( new InputStreamReader( src, encoding ), docType );
    }

    public <T> T fromJson( final String src, final Class<T> type )
    {
        return getGson().fromJson( src, type );
    }

    public <T> T fromJson( final InputStream src, final String encoding, final Class<T> type )
        throws UnsupportedEncodingException
    {
        return getGson().fromJson( new InputStreamReader( src, encoding ), type );
    }

    protected GsonBuilder newGsonBuilder()
    {
        GsonBuilder builder = new GsonBuilder();
        // builder.setPrettyPrinting();
        builder.registerTypeAdapter( CouchDocumentAction.class, new CouchDocumentActionAdapter() );

        return builder;
    }

    protected final Gson getGson()
    {
        return newGsonBuilder().create();
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

}
