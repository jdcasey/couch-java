package org.commonjava.couch.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileAttachment
    implements Attachment
{

    private final File data;

    private final String name;

    private final String contentType;

    private final long contentLength;

    private transient FileInputStream stream;

    public FileAttachment( final String name, final File data, final String contentType,
                           final long contentLength )
    {
        this.name = name;
        this.data = data;
        this.contentType = contentType;
        this.contentLength = contentLength;
    }

    @Override
    public synchronized InputStream getData()
        throws IOException
    {
        if ( stream == null )
        {
            stream = new FileInputStream( data );
        }

        return stream;
    }

    @Override
    public long getContentLength()
    {
        return contentLength;
    }

    @Override
    public String getContentType()
    {
        return contentType;
    }

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public void close()
        throws IOException
    {
        if ( stream != null )
        {
            stream.close();
        }
    }

}
