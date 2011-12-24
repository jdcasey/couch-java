package org.commonjava.couch.model;

import java.util.List;

public interface DocWithAttachments
    extends CouchDocument
{

    List<AttachmentInfo> getAttachments();

}
