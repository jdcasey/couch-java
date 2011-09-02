package org.commonjava.web.maven.proxy.rest;

import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.JAXBElement;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.commonjava.auth.couch.model.Permission;
import org.commonjava.util.logging.Logger;
import org.commonjava.web.maven.proxy.data.ProxyDataException;
import org.commonjava.web.maven.proxy.data.ProxyDataManager;
import org.commonjava.web.maven.proxy.model.Proxy;

@Path( "/admin/proxy" )
@RequestScoped
@RequiresAuthentication
public class ProxyAdminResource
{

    private final Logger logger = new Logger( getClass() );

    @Inject
    private ProxyDataManager proxyManager;

    @Context
    private UriInfo uriInfo;

    @POST
    @Consumes( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, MediaType.TEXT_XML } )
    public Response createProxy( final JAXBElement<Proxy> element )
    {
        SecurityUtils.getSubject().checkPermission( Permission.name( Proxy.NAMESPACE,
                                                                     Permission.ADMIN ) );

        Proxy proxy = element.getValue();
        ResponseBuilder builder;
        try
        {
            proxyManager.storeProxy( proxy );
            builder = Response.created( uriInfo.getAbsolutePathBuilder().build( proxy.getName() ) );
        }
        catch ( ProxyDataException e )
        {
            logger.error( "Failed to create proxy: %s. Reason: %s", e, e.getMessage() );
            builder = Response.status( Status.INTERNAL_SERVER_ERROR );
        }

        return builder.build();
    }

    @POST
    @Path( "/{proxyName}" )
    @Consumes( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, MediaType.TEXT_XML } )
    public Response storeProxy( @PathParam( "proxyName" ) final String proxyName,
                                final JAXBElement<Proxy> element )
    {
        SecurityUtils.getSubject().checkPermission( Permission.name( Proxy.NAMESPACE,
                                                                     Permission.ADMIN ) );

        Proxy proxy = element.getValue();
        ResponseBuilder builder;
        try
        {
            Proxy toUpdate = proxyManager.getProxy( proxyName );
            if ( toUpdate == null )
            {
                toUpdate = proxy;
            }
            else
            {
                toUpdate.setRemoteUrl( proxy.getRemoteUrl() );
            }

            proxyManager.storeProxy( toUpdate );
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
    public List<Proxy> getAllProxies()
    {
        try
        {
            return proxyManager.getAllProxies();
        }
        catch ( ProxyDataException e )
        {
            logger.error( e.getMessage(), e );
            throw new WebApplicationException( Status.INTERNAL_SERVER_ERROR );
        }
    }

    @GET
    @Path( "/{proxyName}" )
    public Proxy getProxy( @PathParam( "proxyName" ) final String proxyName )
    {
        try
        {
            return proxyManager.getProxy( proxyName );
        }
        catch ( ProxyDataException e )
        {
            logger.error( e.getMessage(), e );
            throw new WebApplicationException( Status.INTERNAL_SERVER_ERROR );
        }
    }

    @DELETE
    @Path( "/{proxyName}" )
    public Response deleteProxy( @PathParam( "proxyName" ) final String proxyName )
    {
        ResponseBuilder builder;
        try
        {
            proxyManager.deleteProxy( proxyName );
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
