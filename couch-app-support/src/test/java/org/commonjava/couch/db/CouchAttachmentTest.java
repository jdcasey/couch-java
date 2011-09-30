package org.commonjava.couch.db;

import static org.apache.commons.io.IOUtils.closeQuietly;
import static org.apache.commons.io.IOUtils.copy;
import static org.commonjava.couch.fixture.LoggingFixture.setupLogging;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.net.URL;

import org.apache.log4j.Level;
import org.commonjava.couch.fixture.DBFixture;
import org.commonjava.couch.fixture.TestUser;
import org.commonjava.couch.model.Attachment;
import org.commonjava.couch.model.FileAttachment;
import org.commonjava.couch.model.StreamAttachment;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

public class CouchAttachmentTest
{

    @Rule
    public DBFixture fix = new DBFixture();

    @BeforeClass
    public static void initLogging()
    {
        setupLogging( Level.DEBUG );
    }

    @Test
    public void attachSimpleStream()
        throws Exception
    {
        CouchManager mgr = fix.getCouchManager();
        TestUser user = new TestUser( "user", "First", "Last", "nobody@nowhere.com" );

        mgr.store( user, true );

        byte[] data = "This is a test".getBytes( "UTF-8" );
        mgr.attach( user, new StreamAttachment( "dataFile.txt", new ByteArrayInputStream( data ),
                                                "text/plain", data.length ) );
    }

    @Test
    public void attachAndRetrieveSimpleStream()
        throws Exception
    {
        CouchManager mgr = fix.getCouchManager();
        TestUser user = new TestUser( "user", "First", "Last", "nobody@nowhere.com" );

        mgr.store( user, true );

        String file = "dataFile.txt";
        String str = "This is a test";
        byte[] data = str.getBytes( "UTF-8" );

        mgr.attach( user, new StreamAttachment( file, new ByteArrayInputStream( data ),
                                                "text/plain", data.length ) );

        Attachment attachment = mgr.getAttachment( user, file );
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try
        {
            copy( attachment.getData(), baos );
        }
        finally
        {
            closeQuietly( attachment );
        }

        assertThat( attachment.getContentType(), equalTo( "text/plain" ) );
        assertThat( attachment.getContentLength(), equalTo( (long) data.length ) );
        assertThat( attachment.getName(), equalTo( file ) );
        assertThat( new String( baos.toByteArray() ), equalTo( str ) );
    }

    @Test
    public void attachDeleteAndFailToRetrieveSimpleStream()
        throws Exception
    {
        CouchManager mgr = fix.getCouchManager();
        TestUser user = new TestUser( "user", "First", "Last", "nobody@nowhere.com" );

        mgr.store( user, true );

        String file = "dataFile.txt";
        String str = "This is a test";
        byte[] data = str.getBytes( "UTF-8" );

        mgr.attach( user, new StreamAttachment( file, new ByteArrayInputStream( data ),
                                                "text/plain", data.length ) );

        mgr.deleteAttachment( user, file );

        Attachment attachment = mgr.getAttachment( user, file );
        assertThat( attachment, nullValue() );
    }

    @Test
    public void attachAndRetrieveFile()
        throws Exception
    {
        CouchManager mgr = fix.getCouchManager();
        TestUser user = new TestUser( "user", "First", "Last", "nobody@nowhere.com" );

        mgr.store( user, true );

        String filename = "dataFile.txt";
        URL cls =
            getClass().getClassLoader().getResource( getClass().getName().replace( '.', '/' )
                                                         + ".class" );
        File file = new File( cls.getPath() );

        mgr.attach( user,
                    new FileAttachment( filename, file, "application/octet-stream", file.length() ) );

        Attachment attachment = mgr.getAttachment( user, filename );
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try
        {
            copy( attachment.getData(), baos );
        }
        finally
        {
            closeQuietly( attachment );
        }

        assertThat( attachment.getContentType(), equalTo( "application/octet-stream" ) );
        assertThat( attachment.getContentLength(), equalTo( file.length() ) );
        assertThat( attachment.getName(), equalTo( filename ) );
        assertThat( (long) baos.toByteArray().length, equalTo( file.length() ) );
    }

}
