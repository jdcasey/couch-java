package org.commonjava.web.fd.rest;

import static org.apache.commons.io.IOUtils.closeQuietly;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Date;

import javax.enterprise.context.ApplicationScoped;
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
import org.commonjava.web.fd.config.FileDepotConfiguration;
import org.commonjava.web.fd.model.FileInfo;
import org.jboss.resteasy.annotations.LinkHeaderParam;

@Path( "/files" )
@ApplicationScoped
@RequiresAuthentication
public class FileManager
{

    private final Logger logger = new Logger( getClass() );

    @Inject
    @ApplicationScoped
    private FileDepotConfiguration config;

    @PUT
    @Path( "{name}/{description}" )
    public Response save( @PathParam( "name" ) final String filename,
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
            f = new File( config.getUploadDir(), URLDecoder.decode( filename, "UTF-8" ) );
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
    public Response delete( @PathParam( "name" ) final String filename )
    {
        SecurityUtils.getSubject()
                     .checkPermission( "edit:file" );

        File f;
        try
        {
            f = new File( config.getUploadDir(), URLDecoder.decode( filename, "UTF-8" ) );
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

    @GET
    @Path( "list" )
    @Produces( MediaType.TEXT_PLAIN )
    public String list()
    {
        SecurityUtils.getSubject()
                     .checkPermission( "view:file-info" );

        final StringBuilder sb = new StringBuilder();
        for ( final String name : config.getUploadDir()
                                        .list() )
        {
            final File f = new File( config.getUploadDir(), name );
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
    @LinkHeaderParam( rel = "self" )
    @Produces( MediaType.TEXT_XML )
    public FileInfo getFileInfoXml( @PathParam( "name" ) final String filename )
    {
        logger.info( "\n\nXML-INFO: %s. Configuration is: %s\n\n", filename, config );

        return getFileInfo( filename );
    }

    @GET
    @Path( "{name}" )
    @LinkHeaderParam( rel = "self" )
    @Produces( MediaType.TEXT_PLAIN )
    public Response getFileInfoTxt( @PathParam( "name" ) final String filename )
    {
        logger.info( "\n\nTXT-INFO: %s. Configuration is: %s\n\n", filename, config );

        final String result = String.valueOf( getFileInfo( filename ) );

        logger.info( "Result:\n\n%s\n\n", result );

        return Response.ok( result, MediaType.TEXT_PLAIN )
                       .build();
    }

    private FileInfo getFileInfo( final String filename )
    {
        SecurityUtils.getSubject()
                     .checkPermission( "view:file-info" );

        File f;
        try
        {
            f = new File( config.getUploadDir(), URLDecoder.decode( filename, "UTF-8" ) );
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
    public Response getFile( @PathParam( "name" ) final String filename )
    {
        SecurityUtils.getSubject()
                     .checkPermission( "view:file" );

        logger.info( "\n\nDOWNLOAD: %s. Configuration is: %s\n\n", filename, config );

        File f;
        try
        {
            f = new File( config.getUploadDir(), URLDecoder.decode( filename, "UTF-8" ) );
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
