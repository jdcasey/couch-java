package org.commonjava.maven.mdd.db.session;

public class SimpleDependencyDBSession
    implements DependencyDBSession
{

    private static final String DEFAULT_VIEW_APPLICATION = "db-logic";

    private final String baseUrl;

    private final String logicApplication;

    public SimpleDependencyDBSession( final String baseUrl )
    {
        this.baseUrl = baseUrl;
        this.logicApplication = DEFAULT_VIEW_APPLICATION;
    }

    public SimpleDependencyDBSession( final String baseUrl, final String logicApplication )
    {
        this.baseUrl = baseUrl;
        this.logicApplication = logicApplication;
    }

    @Override
    public String getBaseUrl()
    {
        return baseUrl;
    }

    @Override
    public String getLogicApplication()
    {
        return logicApplication;
    }

}
