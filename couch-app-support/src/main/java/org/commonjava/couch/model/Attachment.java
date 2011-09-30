package org.commonjava.couch.model;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;

public interface Attachment
    extends Closeable
{

    InputStream getData()
        throws IOException;

    long getContentLength();

    String getContentType();

    String getName();

}
