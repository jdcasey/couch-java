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

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public abstract class AbstractCouchDocWithAttachments
    extends AbstractCouchDocument
    implements CouchDocument, DocWithAttachments
{
    @Expose( serialize = false, deserialize = true )
    @SerializedName( "_attachments" )
    private List<AttachmentInfo> attachments;

    @Override
    public List<AttachmentInfo> getAttachments()
    {
        return attachments;
    }

    void setAttachments( final List<AttachmentInfo> attachments )
    {
        this.attachments = attachments;
    }

}
