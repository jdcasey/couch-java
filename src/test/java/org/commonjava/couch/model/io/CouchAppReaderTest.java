package org.commonjava.couch.model.io;

import java.io.IOException;

import org.commonjava.couch.model.CouchApp;
import org.junit.Test;

public class CouchAppReaderTest
{

    @Test
    public void readTestApp()
        throws IOException
    {
        CouchApp app = new CouchAppReader().readAppDefinition( "test-app" );

        System.out.println( app );
    }

}
