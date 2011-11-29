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
