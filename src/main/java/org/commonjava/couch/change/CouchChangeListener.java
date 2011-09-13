package org.commonjava.couch.change;

import javax.annotation.PostConstruct;
import javax.inject.Singleton;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;

@Singleton
public class CouchChangeListener
{

    // @Inject
    private CouchChangeDispatcher dispatcher;

    private HttpClient client;

    public CouchChangeListener()
    {}

    public CouchChangeListener( final CouchChangeDispatcher dispatcher )
    {
        this.dispatcher = dispatcher;
        initClient();
    }

    @PostConstruct
    private void initClient()
    {
        client = new DefaultHttpClient();
    }

    public void listen()
    {}

}
