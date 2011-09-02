/*******************************************************************************
 * Copyright (C) 2011  John Casey
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public
 * License along with this program.  If not, see 
 * <http://www.gnu.org/licenses/>.
 ******************************************************************************/
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
import org.commonjava.auth.couch.data.UserDataException;
import org.commonjava.util.logging.Logger;
import org.commonjava.web.common.model.Listing;
import org.commonjava.web.fd.data.WorkspaceDataException;
import org.commonjava.web.fd.data.WorkspaceDataManager;
import org.commonjava.web.fd.model.Workspace;

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
    public Response addWorkspace( final @PathParam( "pathName" ) String pathName,
                                  final JAXBElement<Workspace> element )
    {
        // FIXME: Un-comment this!!
        // SecurityUtils.getSubject()
        // .checkPermission( Permission.name( Workspace.NAMESPACE, Permission.CREATE ) );

        final Workspace ws = element.getValue();
        ws.setPathName( pathName );

        ResponseBuilder builder = null;
        try
        {
            dataManager.storeWorkspace( ws );
            builder = Response.created( uriInfo.getAbsolutePathBuilder().build() );
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
        throws WorkspaceDataException
    {
        // FIXME: Un-comment this!!
        // SecurityUtils.getSubject()
        // .checkPermission( Permission.name( Workspace.NAMESPACE, Permission.ADMIN ) );

        return new Listing<Workspace>( dataManager.getWorkspaces() );
    }

}
