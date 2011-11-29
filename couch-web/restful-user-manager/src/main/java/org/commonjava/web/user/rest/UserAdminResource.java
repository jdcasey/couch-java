/*******************************************************************************
 * Copyright 2011 John Casey
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package org.commonjava.web.user.rest;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.commonjava.auth.couch.data.PasswordManager;
import org.commonjava.auth.couch.data.UserDataException;
import org.commonjava.auth.couch.data.UserDataManager;
import org.commonjava.auth.couch.model.Permission;
import org.commonjava.auth.couch.model.User;
import org.commonjava.util.logging.Logger;
import org.commonjava.web.common.model.Listing;
import org.commonjava.web.common.ser.JsonSerializer;

import com.google.gson.reflect.TypeToken;

@Path( "/admin/user" )
@RequestScoped
@RequiresAuthentication
public class UserAdminResource
{

    private final Logger logger = new Logger( getClass() );

    @Inject
    private UserDataManager dataManager;

    @Inject
    private PasswordManager passwordManager;

    @Inject
    private JsonSerializer jsonSerializer;

    @Context
    private UriInfo uriInfo;

    @Context
    private HttpServletRequest request;

    @POST
    @Consumes( { MediaType.APPLICATION_JSON } )
    public Response create()
    {
        SecurityUtils.getSubject().isPermitted( Permission.name( User.NAMESPACE, Permission.ADMIN ) );

        @SuppressWarnings( "unchecked" )
        User user = jsonSerializer.fromRequestBody( request, User.class );

        logger.info( "\n\nGot user: %s\n\n", user );

        ResponseBuilder builder;
        try
        {
            boolean created = dataManager.storeUser( user, true );
            if ( created )
            {
                builder =
                    Response.created( uriInfo.getAbsolutePathBuilder().path( user.getUsername() ).build() );
            }
            else
            {
                builder =
                    Response.status( Status.CONFLICT ).entity( "User: " + user.getUsername()
                                                                   + " already exists." );
            }
        }
        catch ( UserDataException e )
        {
            logger.error( "Failed to create user: %s. Reason: %s", e, e.getMessage() );
            builder = Response.status( Status.INTERNAL_SERVER_ERROR );
        }

        return builder.build();
    }

    @POST
    @Path( "/{name}" )
    @Consumes( { MediaType.APPLICATION_JSON } )
    public Response store( @PathParam( "name" ) final String name )
    {
        SecurityUtils.getSubject().isPermitted( Permission.name( User.NAMESPACE, Permission.ADMIN ) );

        @SuppressWarnings( "unchecked" )
        User user = jsonSerializer.fromRequestBody( request, User.class );

        logger.info( "\n\nGot user: %s\n\n", user );

        ResponseBuilder builder;
        try
        {
            User toUpdate = dataManager.getUser( name );
            if ( toUpdate == null )
            {
                toUpdate = user;
            }
            else
            {
                toUpdate.setFirstName( user.getFirstName() );
                toUpdate.setLastName( user.getLastName() );
                toUpdate.setEmail( user.getEmail() );
                toUpdate.setPasswordDigest( passwordManager.digestPassword( user.getPasswordDigest() ) );
                toUpdate.setRoleNames( user.getRoles() );
            }

            dataManager.storeUser( toUpdate, false );
            builder = Response.ok( uriInfo.getAbsolutePathBuilder().build( user.getUsername() ) );
        }
        catch ( UserDataException e )
        {
            logger.error( "Failed to save proxy: %s. Reason: %s", e, e.getMessage() );
            builder = Response.status( Status.INTERNAL_SERVER_ERROR );
        }

        return builder.build();
    }

    @GET
    @Path( "/list" )
    @Produces( { MediaType.APPLICATION_JSON } )
    public Response getAll()
    {
        SecurityUtils.getSubject().isPermitted( Permission.name( User.NAMESPACE, Permission.ADMIN ) );

        try
        {
            Listing<User> listing = new Listing<User>( dataManager.getAllUsers() );
            TypeToken<Listing<User>> tt = new TypeToken<Listing<User>>()
            {};

            return Response.ok().entity( jsonSerializer.toString( listing, tt.getType() ) ).build();
        }
        catch ( UserDataException e )
        {
            logger.error( e.getMessage(), e );
            throw new WebApplicationException( Status.INTERNAL_SERVER_ERROR );
        }
    }

    @GET
    @Path( "/{name}" )
    public Response get( @PathParam( "name" ) final String name )
    {
        SecurityUtils.getSubject().isPermitted( Permission.name( User.NAMESPACE, Permission.ADMIN ) );

        try
        {
            User user = dataManager.getUser( name );
            logger.info( "Returning group: %s", user );

            return Response.ok().entity( jsonSerializer.toString( user ) ).build();
        }
        catch ( UserDataException e )
        {
            logger.error( e.getMessage(), e );
            throw new WebApplicationException( Status.INTERNAL_SERVER_ERROR );
        }
    }

    @DELETE
    @Path( "/{name}" )
    public Response delete( @PathParam( "name" ) final String name )
    {
        SecurityUtils.getSubject().isPermitted( Permission.name( User.NAMESPACE, Permission.ADMIN ) );

        ResponseBuilder builder;
        try
        {
            dataManager.deleteUser( name );
            builder = Response.ok();
        }
        catch ( UserDataException e )
        {
            logger.error( e.getMessage(), e );
            builder = Response.status( Status.INTERNAL_SERVER_ERROR );
        }

        return builder.build();
    }

}
