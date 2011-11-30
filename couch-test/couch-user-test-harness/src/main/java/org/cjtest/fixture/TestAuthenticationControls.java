package org.cjtest.fixture;

import javax.inject.Singleton;

import org.commonjava.auth.couch.model.User;

@Singleton
public class TestAuthenticationControls
{

    private boolean doAuthentication = true;

    private boolean autoCreateAuthorizations = true;

    private String user = User.ADMIN;

    public boolean isDoAuthentication()
    {
        return doAuthentication;
    }

    public boolean isAutoCreateAuthorizations()
    {
        return autoCreateAuthorizations;
    }

    public void setDoAuthentication( final boolean doAuthentication )
    {
        this.doAuthentication = doAuthentication;
    }

    public void setAutoCreateAuthorizations( final boolean autoCreateAuthorizations )
    {
        this.autoCreateAuthorizations = autoCreateAuthorizations;
    }

    public String getUser()
    {
        return user;
    }

    public void setUser( final String user )
    {
        this.user = user;
    }

}
