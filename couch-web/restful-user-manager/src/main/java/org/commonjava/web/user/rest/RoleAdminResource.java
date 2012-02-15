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
import org.commonjava.auth.couch.data.UserDataException;
import org.commonjava.auth.couch.data.UserDataManager;
import org.commonjava.couch.rbac.Permission;
import org.commonjava.couch.rbac.Role;
import org.commonjava.util.logging.Logger;
import org.commonjava.web.json.model.Listing;
import org.commonjava.web.json.ser.JsonSerializer;

import com.google.gson.reflect.TypeToken;

@Path( "/admin/role" )
@RequestScoped
@RequiresAuthentication
public class RoleAdminResource
{

    private final Logger logger = new Logger( getClass() );

    @Inject
    private UserDataManager dataManager;

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
        SecurityUtils.getSubject()
                     .isPermitted( Permission.name( Role.NAMESPACE, Permission.ADMIN ) );

        @SuppressWarnings( "unchecked" )
        final Role role = jsonSerializer.fromRequestBody( request, Role.class );

        logger.info( "\n\nGot role: %s\n\n", role );

        ResponseBuilder builder;
        try
        {
            dataManager.storeRole( role );
            builder = Response.created( uriInfo.getAbsolutePathBuilder()
                                               .path( role.getName() )
                                               .build() );
        }
        catch ( final UserDataException e )
        {
            logger.error( "Failed to create proxy: %s. Reason: %s", e, e.getMessage() );
            builder = Response.status( Status.INTERNAL_SERVER_ERROR );
        }

        return builder.build();
    }

    @POST
    @Path( "/{name}" )
    @Consumes( { MediaType.APPLICATION_JSON } )
    public Response store( @PathParam( "name" ) final String name )
    {
        SecurityUtils.getSubject()
                     .isPermitted( Permission.name( Role.NAMESPACE, Permission.ADMIN ) );

        @SuppressWarnings( "unchecked" )
        final Role role = jsonSerializer.fromRequestBody( request, Role.class );

        logger.info( "\n\nGot role: %s\n\n", role );

        ResponseBuilder builder;
        try
        {
            Role toUpdate = dataManager.getRole( name );
            if ( toUpdate == null )
            {
                toUpdate = role;
            }
            else
            {
                toUpdate.setPermissionNames( role.getPermissions() );
            }

            dataManager.storeRole( toUpdate );
            builder = Response.ok( uriInfo.getAbsolutePathBuilder()
                                          .build() );
        }
        catch ( final UserDataException e )
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
        SecurityUtils.getSubject()
                     .isPermitted( Permission.name( Role.NAMESPACE, Permission.ADMIN ) );

        try
        {
            final Listing<Role> listing = new Listing<Role>( dataManager.getAllRoles() );
            final TypeToken<Listing<Role>> tt = new TypeToken<Listing<Role>>()
            {
            };

            return Response.ok()
                           .entity( jsonSerializer.toString( listing, tt.getType() ) )
                           .build();
        }
        catch ( final UserDataException e )
        {
            logger.error( e.getMessage(), e );
            throw new WebApplicationException( Status.INTERNAL_SERVER_ERROR );
        }
    }

    @GET
    @Path( "/{name}" )
    public Response get( @PathParam( "name" ) final String name )
    {
        SecurityUtils.getSubject()
                     .isPermitted( Permission.name( Role.NAMESPACE, Permission.ADMIN ) );

        try
        {
            final Role role = dataManager.getRole( name );
            logger.info( "Returning role: %s", role );

            return Response.ok()
                           .entity( jsonSerializer.toString( role ) )
                           .build();
        }
        catch ( final UserDataException e )
        {
            logger.error( e.getMessage(), e );
            throw new WebApplicationException( Status.INTERNAL_SERVER_ERROR );
        }
    }

    @DELETE
    @Path( "/{name}" )
    public Response delete( @PathParam( "name" ) final String name )
    {
        SecurityUtils.getSubject()
                     .isPermitted( Permission.name( Role.NAMESPACE, Permission.ADMIN ) );

        ResponseBuilder builder;
        try
        {
            dataManager.deleteRole( name );
            builder = Response.ok();
        }
        catch ( final UserDataException e )
        {
            logger.error( e.getMessage(), e );
            builder = Response.status( Status.INTERNAL_SERVER_ERROR );
        }

        return builder.build();
    }

}
