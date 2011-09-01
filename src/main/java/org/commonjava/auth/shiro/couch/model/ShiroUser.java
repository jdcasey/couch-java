package org.commonjava.auth.shiro.couch.model;

import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.commonjava.auth.couch.model.Role;
import org.commonjava.auth.couch.model.User;

public class ShiroUser
    extends User
{

    private static final String DEFAULT_REALM = "default";

    private String realm = DEFAULT_REALM;

    public ShiroUser()
    {}

    public ShiroUser( final String username, final Role... roles )
    {
        super( username, roles );
    }

    public ShiroUser( final String username, final String email, final String firstName,
                      final String lastName, final String passwordDigest )
    {
        super( username, email, firstName, lastName, passwordDigest );
    }

    public String getRealm()
    {
        return realm;
    }

    public void setRealm( final String realm )
    {
        this.realm = realm;
    }

    public static AuthenticationInfo getAuthenticationInfo( final User user )
    {
        String realm = DEFAULT_REALM;
        if ( user instanceof ShiroUser )
        {
            realm = ( (ShiroUser) user ).getRealm();
        }

        return new SimpleAuthenticationInfo( user.getUsername(), user.getPasswordDigest(), realm );
    }

}
