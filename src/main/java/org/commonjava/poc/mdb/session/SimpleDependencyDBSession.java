package org.commonjava.poc.mdb.session;

public class SimpleDependencyDBSession
    implements DependencyDBSession
{

    private final String baseUrl;

    public SimpleDependencyDBSession( final String baseUrl )
    {
        this.baseUrl = baseUrl;
    }

    @Override
    public String getBaseUrl()
    {
        return baseUrl;
    }

}
