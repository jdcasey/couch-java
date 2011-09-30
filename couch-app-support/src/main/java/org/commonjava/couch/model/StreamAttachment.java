package org.commonjava.couch.model;

import java.io.IOException;
import java.io.InputStream;

public class StreamAttachment
    implements Attachment
{

    private final InputStream data;

    private final String name;

    private final String contentType;

    private final long contentLength;

    public StreamAttachment( final String name, final InputStream data, final String contentType,
                             final long contentLength )
    {
        this.name = name;
        this.data = data;
        this.contentType = contentType;
        this.contentLength = contentLength;
    }

    @Override
    public InputStream getData()
        throws IOException
    {
        return data;
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
        data.close();
    }

}
