package org.commonjava.web.maven.proxy.rest.admin;

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
import org.commonjava.auth.couch.model.Permission;
import org.commonjava.util.logging.Logger;
import org.commonjava.web.common.model.Listing;
import org.commonjava.web.common.ser.DenormalizerPostProcessor;
import org.commonjava.web.common.ser.RestSerializer;
import org.commonjava.web.maven.proxy.data.ProxyDataException;
import org.commonjava.web.maven.proxy.data.ProxyDataManager;
import org.commonjava.web.maven.proxy.model.Group;

import com.google.gson.reflect.TypeToken;

@Path( "/admin/group" )
@RequestScoped
@RequiresAuthentication
public class GroupAdminResource
{

    private final Logger logger = new Logger( getClass() );

    @Inject
    private ProxyDataManager proxyManager;

    @Inject
    private RestSerializer restSerializer;

    @Context
    private UriInfo uriInfo;

    @Context
    private HttpServletRequest request;

    @POST
    @Consumes( { MediaType.APPLICATION_JSON } )
    public Response create()
    {
        SecurityUtils.getSubject().checkPermission( Permission.name( Group.NAMESPACE,
                                                                     Permission.ADMIN ) );

        @SuppressWarnings( "unchecked" )
        Group group =
            restSerializer.fromRequestBody( request, Group.class,
                                            new DenormalizerPostProcessor<Group>() );

        logger.info( "\n\nGot group: %s\n\n", group );

        ResponseBuilder builder;
        try
        {
            proxyManager.storeGroup( group );
            builder = Response.created( uriInfo.getAbsolutePathBuilder().build( group.getName() ) );
        }
        catch ( ProxyDataException e )
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
        SecurityUtils.getSubject().checkPermission( Permission.name( Group.NAMESPACE,
                                                                     Permission.ADMIN ) );

        @SuppressWarnings( "unchecked" )
        Group group =
            restSerializer.fromRequestBody( request, Group.class,
                                            new DenormalizerPostProcessor<Group>() );

        ResponseBuilder builder;
        try
        {
            Group toUpdate = proxyManager.getGroup( name );
            if ( toUpdate == null )
            {
                toUpdate = group;
            }
            else
            {
                toUpdate.setConstituents( group.getConstituents() );
            }

            proxyManager.storeGroup( toUpdate );
            builder = Response.created( uriInfo.getAbsolutePathBuilder().build() );
        }
        catch ( ProxyDataException e )
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
        SecurityUtils.getSubject().checkPermission( Permission.name( Group.NAMESPACE,
                                                                     Permission.ADMIN ) );

        try
        {
            Listing<Group> listing = new Listing<Group>( proxyManager.getAllGroups() );
            TypeToken<Listing<Group>> tt = new TypeToken<Listing<Group>>()
            {};

            return Response.ok().entity( restSerializer.toJson( listing, tt.getType() ) ).build();
        }
        catch ( ProxyDataException e )
        {
            logger.error( e.getMessage(), e );
            throw new WebApplicationException( Status.INTERNAL_SERVER_ERROR );
        }
    }

    @GET
    @Path( "/{name}" )
    public Response get( @PathParam( "name" ) final String name )
    {
        SecurityUtils.getSubject().checkPermission( Permission.name( Group.NAMESPACE,
                                                                     Permission.ADMIN ) );

        try
        {
            Group group = proxyManager.getGroup( name );
            logger.info( "Returning group: %s", group );

            return Response.ok().entity( restSerializer.toJson( group ) ).build();
        }
        catch ( ProxyDataException e )
        {
            logger.error( e.getMessage(), e );
            throw new WebApplicationException( Status.INTERNAL_SERVER_ERROR );
        }
    }

    @DELETE
    @Path( "/{name}" )
    public Response delete( @PathParam( "name" ) final String name )
    {
        SecurityUtils.getSubject().checkPermission( Permission.name( Group.NAMESPACE,
                                                                     Permission.ADMIN ) );

        ResponseBuilder builder;
        try
        {
            proxyManager.deleteGroup( name );
            builder = Response.ok();
        }
        catch ( ProxyDataException e )
        {
            logger.error( e.getMessage(), e );
            builder = Response.status( Status.INTERNAL_SERVER_ERROR );
        }

        return builder.build();
    }

}
