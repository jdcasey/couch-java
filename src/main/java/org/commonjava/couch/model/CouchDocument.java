package org.commonjava.couch.model;

public interface CouchDocument
{

    String getCouchDocId();

    String getCouchDocRev();

    void setCouchDocRev( String revision );

}
