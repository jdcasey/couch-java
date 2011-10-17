/*******************************************************************************
 * Copyright (C) 2011  John Casey
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public
 * License along with this program.  If not, see 
 * <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package org.commonjava.couch.change;

import static org.commonjava.couch.util.UrlUtils.buildUrl;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

import javax.enterprise.inject.Alternative;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.commonjava.couch.change.dispatch.CouchChangeDispatcher;
import org.commonjava.couch.conf.CouchDBConfiguration;
import org.commonjava.couch.db.CouchDBException;
import org.commonjava.couch.db.CouchManager;
import org.commonjava.couch.io.CouchHttpClient;
import org.commonjava.couch.io.Serializer;
import org.commonjava.couch.model.AbstractCouchDocument;
import org.commonjava.couch.model.CouchDocRef;
import org.commonjava.util.logging.Logger;

import com.google.gson.annotations.SerializedName;

@Named( "dont-use-directly" )
@Alternative
public class CouchChangeListener
    implements Runnable
{

    private static final String CHANGE_LISTENER_DOCID = "change-listener-metadata";

    private static final String CHANGES_SERVICE = "_changes";

    private final Logger logger = new Logger( getClass() );

    @Inject
    private CouchChangeDispatcher dispatcher;

    @Inject
    private CouchDBConfiguration config;

    @Inject
    private CouchHttpClient http;

    @Inject
    private CouchManager couch;

    @Inject
    private Serializer serializer;

    private ChangeListenerMetadata metadata;

    private Thread listenerThread;

    private boolean running = false;

    private final Object internalLock = new Object();

    public CouchChangeListener()
    {}

    public CouchChangeListener( final CouchChangeDispatcher dispatcher, final CouchHttpClient http,
                                final CouchDBConfiguration config, final CouchManager couch,
                                final Serializer serializer )
    {
        this.dispatcher = dispatcher;
        this.http = http;
        this.config = config;
        this.couch = couch;
        this.serializer = serializer;
    }

    public void startup()
        throws CouchDBException
    {
        startup( true );
    }

    public void startup( final boolean wait )
        throws CouchDBException
    {
        metadata =
            couch.getDocument( new CouchDocRef( CHANGE_LISTENER_DOCID ),
                               ChangeListenerMetadata.class );
        if ( metadata == null )
        {
            metadata = new ChangeListenerMetadata();
        }

        listenerThread = new Thread( this );
        listenerThread.setDaemon( true );
        listenerThread.start();

        if ( wait )
        {
            synchronized ( internalLock )
            {
                while ( !running )
                {
                    logger.info( "Waiting for change listener to startup..." );
                    try
                    {
                        internalLock.wait( 100 );
                    }
                    catch ( InterruptedException e )
                    {
                        logger.info( "Interrupted..." );
                        break;
                    }
                }
            }
        }
    }

    public void shutdown()
        throws CouchDBException
    {
        if ( listenerThread != null )
        {
            listenerThread.interrupt();

            while ( listenerThread.isAlive() )
            {
                logger.info( "Waiting for change-listener shutdown..." );
                synchronized ( internalLock )
                {
                    try
                    {
                        internalLock.wait( 2000 );
                    }
                    catch ( InterruptedException e )
                    {
                        break;
                    }
                }
            }
        }

        if ( metadata != null )
        {
            if ( metadata.getLastProcessedSequenceId() < 1 )
            {
                couch.store( metadata, false );
            }
        }

        running = false;

        synchronized ( this )
        {
            notifyAll();
        }
    }

    public boolean isRunning()
    {
        return running;
    }

    @Override
    public void run()
    {
        CouchDocChangeDeserializer docDeserializer = new CouchDocChangeDeserializer();

        all: while ( !Thread.interrupted() )
        {
            HttpGet get;
            try
            {
                String url =
                    buildUrl( config.getDatabaseUrl(), metadata.getUrlParameters(), CHANGES_SERVICE );

                get = new HttpGet( url );
            }
            catch ( MalformedURLException e )
            {
                logger.error( "Failed to construct changes URL for db: %s. Reason: %s", e,
                              config.getDatabaseUrl(), e.getMessage() );
                break;
            }

            String encoding = null;
            try
            {
                HttpResponse response =
                    http.executeHttpWithResponse( get, "Failed to open changes stream." );

                if ( response.getEntity() == null )
                {
                    logger.error( "Changes stream did not return a response body." );
                    break;
                }

                Header encodingHeader = response.getEntity().getContentEncoding();
                if ( encodingHeader == null )
                {
                    encoding = "UTF-8";
                }
                else
                {
                    encoding = encodingHeader.getValue();
                }

                InputStream stream = response.getEntity().getContent();

                running = true;
                synchronized ( internalLock )
                {
                    internalLock.notifyAll();
                }

                CouchDocChangeList changes =
                    serializer.fromJson( stream, encoding, CouchDocChangeList.class,
                                         docDeserializer );

                for ( CouchDocChange change : changes )
                {
                    if ( !change.getId().equals( CHANGE_LISTENER_DOCID ) )
                    {
                        metadata.setLastProcessedSequenceId( change.getSequence() );
                        dispatcher.documentChanged( change );
                    }
                }

            }
            catch ( CouchDBException e )
            {
                logger.error( "Failed to read changes stream for db: %s. Reason: %s", e,
                              config.getDatabaseUrl(), e.getMessage() );
                break;
            }
            catch ( UnsupportedEncodingException e )
            {
                logger.error( "Invalid content encoding for changes response: %s. Reason: %s", e,
                              encoding, e.getMessage() );
                break;
            }
            catch ( IOException e )
            {
                logger.error( "Error reading changes response content. Reason: %s", e,
                              e.getMessage() );
                break;
            }
            finally
            {
                http.cleanup( get );
            }

            try
            {
                Thread.sleep( 2000 );
            }
            catch ( InterruptedException e )
            {
                break all;
            }
        }

        synchronized ( internalLock )
        {
            internalLock.notifyAll();
        }
    }

    static final class ChangeListenerMetadata
        extends AbstractCouchDocument
    {

        @SerializedName( "last_seq" )
        private int lastProcessedSequenceId;

        ChangeListenerMetadata()
        {
            setCouchDocId( CHANGE_LISTENER_DOCID );
        }

        public Map<String, String> getUrlParameters()
        {
            Map<String, String> params = new HashMap<String, String>();
            // params.put( "feed", "continuous" );
            if ( lastProcessedSequenceId > 0 )
            {
                params.put( "since", Integer.toString( lastProcessedSequenceId ) );
            }

            return params;
        }

        int getLastProcessedSequenceId()
        {
            return lastProcessedSequenceId;
        }

        void setLastProcessedSequenceId( final int lastProcessedSequenceId )
        {
            this.lastProcessedSequenceId = lastProcessedSequenceId;
        }

    }

}
