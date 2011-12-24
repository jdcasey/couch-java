package org.commonjava.couch.model;

public class AttachmentInfo
{

    private final String name;

    private final String contentType;

    private final int contentLength;

    public AttachmentInfo( final String name, final String contentType, final int contentLength )
    {
        this.name = name;
        this.contentType = contentType;
        this.contentLength = contentLength;
    }

    public String getName()
    {
        return name;
    }

    public String getContentType()
    {
        return contentType;
    }

    public int getContentLength()
    {
        return contentLength;
    }

}
