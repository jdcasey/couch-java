package org.commonjava.web.user.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriBuilder;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.commonjava.util.logging.Logger;

@Path( "/session" )
public class SessionResource
{

    private final Logger logger = new Logger( getClass() );

    @GET
    @Path( "/logout" )
    public Response logout()
    {
        final Subject subject = SecurityUtils.getSubject();
        if ( subject.isAuthenticated() )
        {
            subject.logout();
            return Response.ok()
                           .build();
        }

        return Response.notModified()
                       .build();
    }

    // @GET
    // @Path( "/whoami" )
    // public Response whoAmI()
    // {
    // final Subject subject = SecurityUtils.getSubject();
    // if ( subject.isAuthenticated() )
    // {
    // subject.getSession().
    // }
    // }

    @GET
    @Path( "/login" )
    public Response login( @QueryParam( "u" ) final String user, @QueryParam( "p" ) final String passwordDigest,
                           @QueryParam( "r" ) final String rememberMe, @Context final UriBuilder uriBuilder )
    {
        if ( user == null || passwordDigest == null )
        {
            return Response.status( Status.BAD_REQUEST )
                           .build();
        }

        ResponseBuilder builder = null;

        final Subject subject = SecurityUtils.getSubject();
        if ( subject.isAuthenticated() )
        {
            builder = Response.status( Status.CONFLICT )
                              .entity( "A user is already logged in. Logout first." )
                              .location( uriBuilder.path( getClass(), "logout" )
                                                   .build() );
        }
        else
        {
            final UsernamePasswordToken token = new UsernamePasswordToken( user, passwordDigest );
            token.setRememberMe( rememberMe == null ? false : Boolean.parseBoolean( rememberMe ) );
            try
            {
                subject.login( token );
                builder = Response.ok();
            }
            catch ( final AuthenticationException e )
            {
                logger.error( "Failed to login user: '%s'. Reason: %s", e, user, e.getMessage() );
                builder = Response.serverError();
            }
        }

        return builder == null ? Response.serverError()
                                         .build() : builder.build();
    }

}
