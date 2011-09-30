package org.commonjava.couch.db.model;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.commonjava.couch.db.CouchDBException;
import org.commonjava.couch.io.CouchHttpClient;
import org.commonjava.couch.model.Attachment;

public class AttachmentDownload
    implements Attachment
{

    private final HttpResponse response;

    private final CouchHttpClient client;

    private final String name;

    private final HttpGet request;

    public AttachmentDownload( final String name, final HttpGet request,
                               final HttpResponse response, final CouchHttpClient client )
        throws CouchDBException
    {
        try
        {
            if ( response.getEntity() == null || response.getEntity().getContent() == null )
            {
                throw new CouchDBException( "Invalid attachment; response contained no data: %s",
                                            name );
            }
        }
        catch ( IOException e )
        {
            throw new CouchDBException(
                                        "Invalid attachment; response data cannot be read for: %s. Reason: %s",
                                        e, name, e.getMessage() );
        }

        this.name = name;
        this.request = request;
        this.response = response;
        this.client = client;
    }

    @Override
    public InputStream getData()
        throws IOException
    {
        return new ConnectionManagingInputStream();
    }

    @Override
    public long getContentLength()
    {
        return response.getEntity().getContentLength();
    }

    @Override
    public String getContentType()
    {
        Header[] headers = response.getHeaders( HttpHeaders.CONTENT_TYPE );
        if ( headers != null && headers.length > 0 )
        {
            return headers[0].getValue();
        }

        return null;
    }

    @Override
    public String getName()
    {
        return name;
    }

    private final class ConnectionManagingInputStream
        extends InputStream
    {

        InputStream stream;

        public ConnectionManagingInputStream()
            throws IOException
        {
            stream = response.getEntity().getContent();
        }

        @Override
        public int read()
            throws IOException
        {
            return stream.read();
        }

        @Override
        public void close()
            throws IOException
        {
            try
            {
                super.close();
                stream.close();
            }
            finally
            {
                client.cleanup( request );
            }
        }

    }

    @Override
    public void close()
        throws IOException
    {
        client.cleanup( request );
    }

}
