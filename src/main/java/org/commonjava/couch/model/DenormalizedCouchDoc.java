package org.commonjava.couch.model;

public interface DenormalizedCouchDoc
{

    void calculateDenormalizedFields()
        throws DenormalizationException;

}
