package org.commonjava.auth.shiro.couch.model;

import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.commonjava.auth.couch.model.User;

public class ShiroUser
    extends User
{

    private static final String DEFAULT_REALM = "default";

    public static AuthenticationInfo getAuthenticationInfo( final User user )
    {
        // TODO: make the user able to store properties, so we can set different realms...?
        String realm = DEFAULT_REALM;
        return new SimpleAuthenticationInfo( user.getUsername(), user.getPasswordDigest(), realm );
    }

    public static AuthenticationToken getAuthenticationToken( final User user )
    {
        return new UsernamePasswordToken( user.getUsername(), user.getPasswordDigest() );
    }

}
