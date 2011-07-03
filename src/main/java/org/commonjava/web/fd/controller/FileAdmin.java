package org.commonjava.web.fd.controller;

import static org.apache.commons.io.IOUtils.closeQuietly;
import static org.apache.commons.io.IOUtils.copy;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

import javax.enterprise.inject.Model;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;

import org.commonjava.util.logging.Logger;
import org.commonjava.web.fd.config.FileDepotConfiguration;
import org.commonjava.web.fd.data.WorkspaceSession;
import org.commonjava.web.fd.model.FileInfo;
import org.commonjava.web.fd.rest.FileRESTManager;
import org.richfaces.event.FileUploadEvent;
import org.richfaces.model.UploadedFile;

@Model
public class FileAdmin
{
    private final Logger log = new Logger( getClass() );

    @Inject
    private FileDepotConfiguration config;

    @Inject
    private FileRESTManager fileManager;

    @Inject
    private WorkspaceSession workspaceSession;

    @Produces
    @Named
    public List<FileInfo> getFiles()
    {
        final List<FileInfo> files;
        if ( workspaceSession.getCurrentWorkspaceId() != null )
        {
            files = fileManager.getFiles( workspaceSession.getCurrentWorkspaceId() );
        }
        else
        {
            files = Collections.emptyList();
        }

        return files;
    }

    public void listener( final FileUploadEvent uploadEvent )
    {
        final UploadedFile f = uploadEvent.getUploadedFile();

        if ( workspaceSession.getCurrentWorkspaceId() == null )
        {
            throw new WebApplicationException( Status.BAD_REQUEST );
        }

        final File dir = new File( config.getUploadDirectory(), workspaceSession.getCurrentWorkspaceId()
                                                                                .toString() );
        if ( ( !dir.exists() || !dir.isDirectory() ) && !dir.mkdirs() )
        {
            log.error( "\n\n\nFailed to create directory: %s", dir );
            throw new WebApplicationException( Status.INTERNAL_SERVER_ERROR );
        }

        final File dest = new File( dir, f.getName() );

        log.info( "\n\n\nSaving: %s\nSize: %s\nTo: %s\n\n\n", f.getName(), f.getSize(), dest );

        final InputStream in = f.getInputStream();
        FileOutputStream out = null;
        try
        {
            out = new FileOutputStream( dest );
            copy( in, out );
        }
        catch ( final IOException e )
        {
            log.error( "Failed to save: %s. Reason: %s", e, dest, e.getMessage() );
            throw new WebApplicationException( Status.INTERNAL_SERVER_ERROR );
        }
        finally
        {
            closeQuietly( in );
            closeQuietly( out );
        }
    }

    public String save()
    {
        log.info( "\n\n\nSaved.\n\n\n" );
        return null;
    }

}