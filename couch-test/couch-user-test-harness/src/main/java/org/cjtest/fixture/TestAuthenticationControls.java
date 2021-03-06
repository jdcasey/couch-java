package org.cjtest.fixture;

import javax.inject.Singleton;

import org.commonjava.couch.rbac.User;

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
        System.out.println( "\n\n\nSetting user: '" + user + "' in test-login controls" );
        this.user = user;
    }

    public void resetUser()
    {
        this.user = User.ADMIN;
    }

    @Override
    public String toString()
    {
        return String.format( "TestAuthenticationControls [doAuthentication=%s, autoCreateAuthorizations=%s, user=%s]",
                              doAuthentication, autoCreateAuthorizations, user );
    }

}
