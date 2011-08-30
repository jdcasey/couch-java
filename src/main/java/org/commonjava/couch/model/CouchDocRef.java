package org.commonjava.couch.model;

public class CouchDocRef
    extends AbstractCouchDocument
{
    public CouchDocRef( final String id, final String rev )
    {
        setCouchDocId( id );
        setCouchDocRev( rev );
    }

    public CouchDocRef( final String id )
    {
        setCouchDocId( id );
    }

}
