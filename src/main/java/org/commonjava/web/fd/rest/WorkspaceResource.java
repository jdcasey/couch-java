package org.commonjava.web.fd.rest;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.JAXBElement;

import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.commonjava.util.logging.Logger;
import org.commonjava.web.common.model.Listing;
import org.commonjava.web.fd.data.WorkspaceDataException;
import org.commonjava.web.fd.data.WorkspaceDataManager;
import org.commonjava.web.fd.model.Workspace;
import org.commonjava.web.user.data.UserDataException;

@Path( "/workspaces" )
@RequestScoped
@RequiresAuthentication
public class WorkspaceResource
{

    private final Logger logger = new Logger( getClass() );

    @Inject
    private WorkspaceDataManager dataManager;

    @Context
    private UriInfo uriInfo;

    @PUT
    @Path( "{pathName}" )
    @Consumes( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, MediaType.TEXT_XML } )
    public Response addWorkspace( final @PathParam( "pathName" ) String pathName, final JAXBElement<Workspace> element )
    {
        // FIXME: Un-comment this!!
        // SecurityUtils.getSubject()
        // .checkPermission( Permission.name( Workspace.NAMESPACE, Permission.CREATE ) );

        final Workspace ws = element.getValue();
        ws.setPathName( pathName );

        ResponseBuilder builder = null;
        try
        {
            dataManager.saveWorkspace( ws, true );
            builder = Response.created( uriInfo.getAbsolutePathBuilder()
                                               .build() );
        }
        catch ( final WorkspaceDataException e )
        {
            logger.error( "Failed to add workspace: %s. Reason: %s", e, ws, e.getMessage() );
            builder = Response.serverError();
        }
        catch ( final UserDataException e )
        {
            logger.error( "Failed to add workspace: %s. Reason: %s", e, ws, e.getMessage() );
            builder = Response.serverError();
        }

        return builder.build();
    }

    @GET
    @Path( "list" )
    @Produces( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, MediaType.TEXT_XML } )
    public Listing<Workspace> getWorkspaces()
    {
        // FIXME: Un-comment this!!
        // SecurityUtils.getSubject()
        // .checkPermission( Permission.name( Workspace.NAMESPACE, Permission.ADMIN ) );

        return new Listing<Workspace>( dataManager.getWorkspaces() );
    }

}
