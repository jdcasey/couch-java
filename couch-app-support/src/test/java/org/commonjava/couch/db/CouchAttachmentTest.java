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
package org.commonjava.couch.db;

import static org.apache.commons.io.IOUtils.closeQuietly;
import static org.apache.commons.io.IOUtils.copy;
import static org.commonjava.couch.fixture.LoggingFixture.setupLogging;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.net.URL;
import java.util.List;

import org.apache.log4j.Level;
import org.commonjava.couch.fixture.DBFixture;
import org.commonjava.couch.fixture.TestUser;
import org.commonjava.couch.model.Attachment;
import org.commonjava.couch.model.AttachmentInfo;
import org.commonjava.couch.model.CouchDocRef;
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
        final CouchManager mgr = fix.getCouchManager();
        final TestUser user = new TestUser( "user", "First", "Last", "nobody@nowhere.com" );

        mgr.store( user, true );

        final byte[] data = "This is a test".getBytes( "UTF-8" );
        mgr.attach( user, new StreamAttachment( "dataFile.txt", new ByteArrayInputStream( data ), "text/plain",
                                                data.length ) );
    }

    @Test
    public void attachAndListSimpleStream()
        throws Exception
    {
        final CouchManager mgr = fix.getCouchManager();
        final TestUser user = new TestUser( "user", "First", "Last", "nobody@nowhere.com" );

        mgr.store( user, true );

        final byte[] data = "This is a test".getBytes( "UTF-8" );
        mgr.attach( user, new StreamAttachment( "dataFile.txt", new ByteArrayInputStream( data ), "text/plain",
                                                data.length ) );

        final TestUser userOut = mgr.getDocument( new CouchDocRef( "user" ), TestUser.class );

        final List<AttachmentInfo> attachments = userOut.getAttachments();

        assertThat( attachments, notNullValue() );
        assertThat( attachments.size(), equalTo( 1 ) );

        final AttachmentInfo info = attachments.get( 0 );
        assertThat( info, notNullValue() );
        assertThat( info.getName(), equalTo( "dataFile.txt" ) );
    }

    @Test
    public void attachAndRetrieveSimpleStream()
        throws Exception
    {
        final CouchManager mgr = fix.getCouchManager();
        final TestUser user = new TestUser( "user", "First", "Last", "nobody@nowhere.com" );

        mgr.store( user, true );

        final String file = "dataFile.txt";
        final String str = "This is a test";
        final byte[] data = str.getBytes( "UTF-8" );

        mgr.attach( user, new StreamAttachment( file, new ByteArrayInputStream( data ), "text/plain", data.length ) );

        final Attachment attachment = mgr.getAttachment( user, file );
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
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
        final CouchManager mgr = fix.getCouchManager();
        final TestUser user = new TestUser( "user", "First", "Last", "nobody@nowhere.com" );

        mgr.store( user, true );

        final String file = "dataFile.txt";
        final String str = "This is a test";
        final byte[] data = str.getBytes( "UTF-8" );

        mgr.attach( user, new StreamAttachment( file, new ByteArrayInputStream( data ), "text/plain", data.length ) );

        mgr.deleteAttachment( user, file );

        final Attachment attachment = mgr.getAttachment( user, file );
        assertThat( attachment, nullValue() );
    }

    @Test
    public void attachAndRetrieveFile()
        throws Exception
    {
        final CouchManager mgr = fix.getCouchManager();
        final TestUser user = new TestUser( "user", "First", "Last", "nobody@nowhere.com" );

        mgr.store( user, true );

        final String filename = "dataFile.txt";
        final URL cls = getClass().getClassLoader()
                                  .getResource( getClass().getName()
                                                          .replace( '.', '/' ) + ".class" );
        final File file = new File( cls.getPath() );

        mgr.attach( user, new FileAttachment( filename, file, "application/octet-stream", file.length() ) );

        final Attachment attachment = mgr.getAttachment( user, filename );
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
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
