package org.commonjava.web.maven.proxy.rest.access;

import java.io.File;
import java.util.List;

import javax.activation.MimetypesFileTypeMap;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.commonjava.auth.couch.model.Permission;
import org.commonjava.util.logging.Logger;
import org.commonjava.web.maven.proxy.data.ProxyDataException;
import org.commonjava.web.maven.proxy.data.ProxyDataManager;
import org.commonjava.web.maven.proxy.model.Group;
import org.commonjava.web.maven.proxy.model.Repository;
import org.commonjava.web.maven.proxy.rest.util.Downloader;

@Path( "/group" )
@RequestScoped
@RequiresAuthentication
public class GroupAccessResource
{
    private final Logger logger = new Logger( getClass() );

    @Inject
    private ProxyDataManager proxyManager;

    @Inject
    private Downloader downloader;

    // @Context
    // private UriInfo uriInfo;

    @GET
    @Path( "/{name}{path: (/.+)?}" )
    public Response getProxyContent( @PathParam( "name" ) final String name,
                                     @PathParam( "path" ) final String path )
    {
        SecurityUtils.getSubject().checkPermission( Permission.name( Group.NAMESPACE, name,
                                                                     Permission.READ ) );

        // TODO:
        // 1. directory request (ends with "/")...browse somehow??
        // 2. empty path (directory request for proxy root)

        List<Repository> repos;

        try
        {
            Group group = proxyManager.getGroup( name );
            if ( group == null )
            {
                throw new WebApplicationException(
                                                   Response.status( Status.NOT_FOUND ).entity( "Repository group: "
                                                                                                   + name
                                                                                                   + " not found." ).build() );
            }

            repos = proxyManager.getRepositoriesForGroup( name );
        }
        catch ( ProxyDataException e )
        {
            logger.error( "Failed to retrieve repository-group information: %s. Reason: %s", e,
                          name, e.getMessage() );
            throw new WebApplicationException(
                                               Response.status( Status.INTERNAL_SERVER_ERROR ).build() );
        }

        File target = downloader.download( repos, path );

        String mimeType = new MimetypesFileTypeMap().getContentType( target );
        return Response.ok( target, mimeType ).build();
    }

}
