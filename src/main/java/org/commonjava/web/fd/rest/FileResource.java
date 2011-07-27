/*******************************************************************************
 * Copyright (C) 2011 John Casey.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package org.commonjava.web.fd.rest;

import static org.apache.commons.io.IOUtils.closeQuietly;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriBuilder;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.commonjava.util.logging.Logger;
import org.commonjava.web.common.model.Listing;
import org.commonjava.web.fd.config.FileDepotConfiguration;
import org.commonjava.web.fd.data.WorkspaceDataManager;
import org.commonjava.web.fd.model.FileInfo;
import org.commonjava.web.fd.model.Workspace;
import org.commonjava.web.user.model.Permission;
import org.commonjava.web.user.model.Permission;

@Path( "/files/{workspaceId}" )
@RequestScoped
@RequiresAuthentication
public class FileResource
{

    private final Logger logger = new Logger( getClass() );

    @Inject
    private FileDepotConfiguration config;

    @Inject
    private WorkspaceDataManager wsDataManager;

    @PUT
    @Path( "{name}/{description}" )
    public Response save( @PathParam( "workspaceId" ) final Long workspaceId,
                          @PathParam( "name" ) final String filename,
                          @PathParam( "description" ) final String description,
                          @Context final HttpServletRequest request )
    {
        InputStream in = null;
        try
        {
            in = request.getInputStream();
        }
        catch ( final IOException e )
        {
            logger.error( "Failed to get input stream from request: %s", e, e.getMessage() );
            throw new WebApplicationException( Status.BAD_REQUEST );
        }

        File f;
        try
        {
            final File dir = new File( config.getUploadDirectory(), workspaceId.toString() );
            f = new File( dir, URLDecoder.decode( filename, "UTF-8" ) );
        }
        catch ( final UnsupportedEncodingException e )
        {
            logger.error( "Failed to decode filename: %s", e, e.getMessage() );
            throw new WebApplicationException( Status.BAD_REQUEST );
        }

        if ( f.exists() )
        {
            logger.error( "File already exists: %s", f.getName() );
            throw new WebApplicationException( Status.CONFLICT );
        }

        f.getParentFile()
         .mkdirs();

        FileOutputStream out = null;
        try
        {
            out = new FileOutputStream( f );
        }
        catch ( final IOException e )
        {
            logger.error( "Cannot write file: %s. Reason: %s", e, f.getName(), e.getMessage() );
            throw new WebApplicationException( Status.INTERNAL_SERVER_ERROR );
        }
        finally
        {
            closeQuietly( in );
            closeQuietly( out );
        }

        return Response.created( UriBuilder.fromResource( getClass() )
                                           .build( filename ) )
                       .build();
    }

    @DELETE
    @Path( "{name}" )
    public Response delete( @PathParam( "workspaceId" ) final Long workspaceId,
                            @PathParam( "name" ) final String filename )
    {
        SecurityUtils.getSubject()
                     .checkPermission( "edit:file" );

        File f;
        try
        {
            final File dir = new File( config.getUploadDirectory(), workspaceId.toString() );
            f = new File( dir, URLDecoder.decode( filename, "UTF-8" ) );
        }
        catch ( final UnsupportedEncodingException e )
        {
            logger.error( "Failed to decode filename: %s", e, e.getMessage() );
            throw new WebApplicationException( Status.BAD_REQUEST );
        }

        if ( f.exists() )
        {
            if ( f.delete() )
            {
                return Response.ok()
                               .build();
            }
            else
            {
                throw new WebApplicationException( Status.NOT_MODIFIED );
            }
        }
        else
        {
            logger.info( "File not found: %s", f );
            throw new WebApplicationException( Status.NOT_FOUND );
        }
    }

    public Listing<FileInfo> getFiles( final Long workspaceId )
    {
        final List<FileInfo> result = new ArrayList<FileInfo>();
        final File dir = new File( config.getUploadDirectory(), workspaceId.toString() );

        if ( dir.exists() )
        {
            for ( final String name : dir.list() )
            {
                final File f = new File( dir, name );
                result.add( new FileInfo( f ) );
            }
        }

        return new Listing<FileInfo>( result );
    }

    @GET
    @Path( "list" )
    @Produces( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, MediaType.TEXT_XML } )
    public Listing<FileInfo> list( @PathParam( "workspaceId" ) final Long workspaceId )
    {
        final Workspace ws = wsDataManager.getWorkspace( workspaceId );

        SecurityUtils.getSubject()
                     .checkPermission( Permission.name( Workspace.NAMESPACE, ws.getPathName(), Permission.READ ) );

        return getFiles( workspaceId );
    }

    @GET
    @Path( "list" )
    @Produces( MediaType.TEXT_PLAIN )
    public String listText( @PathParam( "workspaceId" ) final Long workspaceId )
    {
        SecurityUtils.getSubject()
                     .checkPermission( "view:file-info" );

        final StringBuilder sb = new StringBuilder();
        for ( final FileInfo f : getFiles( workspaceId ) )
        {
            if ( sb.length() > 0 )
            {
                sb.append( "\n" );
            }

            sb.append( f.length() )
              .append( "  " )
              .append( new Date( f.lastModified() ) )
              .append( "  " )
              .append( f.getName() );
        }

        return sb.toString();
    }

    @GET
    @Path( "{name}" )
    @Produces( { MediaType.TEXT_XML, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML } )
    public FileInfo getFileInfo( @PathParam( "workspaceId" ) final Long workspaceId,
                                 @PathParam( "name" ) final String filename )
    {
        logger.info( "\n\nXML-INFO: %s. Configuration is: %s\n\n", filename, config );

        return _getFileInfo( workspaceId, filename );
    }

    @GET
    @Path( "{name}" )
    @Produces( MediaType.TEXT_PLAIN )
    public Response getFileInfoText( @PathParam( "workspaceId" ) final Long workspaceId,
                                     @PathParam( "name" ) final String filename )
    {
        logger.info( "\n\nTXT-INFO: %s. Configuration is: %s\n\n", filename, config );

        final String result = String.valueOf( _getFileInfo( workspaceId, filename ) );

        logger.info( "Result:\n\n%s\n\n", result );

        return Response.ok( result, MediaType.TEXT_PLAIN )
                       .build();
    }

    private FileInfo _getFileInfo( final Long workspaceId, final String filename )
    {
        SecurityUtils.getSubject()
                     .checkPermission( "view:file-info" );

        File f;
        try
        {
            final File dir = new File( config.getUploadDirectory(), workspaceId.toString() );
            f = new File( dir, URLDecoder.decode( filename, "UTF-8" ) );
        }
        catch ( final UnsupportedEncodingException e )
        {
            logger.error( "Failed to decode filename: %s", e, e.getMessage() );
            throw new WebApplicationException( Status.BAD_REQUEST );
        }

        if ( f.exists() )
        {
            return new FileInfo( f );
        }
        else
        {
            logger.error( "File not found: %s", f );
            throw new WebApplicationException( Status.NOT_FOUND );
        }
    }

    @GET
    @Path( "{name}/data" )
    @Produces( MediaType.APPLICATION_OCTET_STREAM )
    public Response getFile( @PathParam( "workspaceId" ) final Long workspaceId,
                             @PathParam( "name" ) final String filename )
    {
        SecurityUtils.getSubject()
                     .checkPermission( "view:file" );

        logger.info( "\n\nDOWNLOAD: %s. Configuration is: %s\n\n", filename, config );

        File f;
        try
        {
            final File dir = new File( config.getUploadDirectory(), workspaceId.toString() );
            f = new File( dir, URLDecoder.decode( filename, "UTF-8" ) );
        }
        catch ( final UnsupportedEncodingException e )
        {
            logger.error( "Failed to decode filename: %s", e, e.getMessage() );
            return Response.status( Status.BAD_REQUEST )
                           .build();
        }

        if ( f.exists() )
        {
            return Response.ok( f )
                           .header( "Content-Disposition", "attachment; filename=\"" + filename + "\"" )
                           // .header( "Content-Disposition", "inline; filename=\"" + filename + "\"" )
                           .build();
        }
        else
        {
            return Response.status( Status.NOT_FOUND )
                           .build();
        }
    }

}
