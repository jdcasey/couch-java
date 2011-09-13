package org.commonjava.couch.conf;

public interface CouchDBConfiguration
{

    String getDatabaseUrl();

    String getDatabaseUser();

    String getDatabasePassword();

    String getDatabaseHost();

    int getDatabasePort();

    int getMaxConnections();

}
