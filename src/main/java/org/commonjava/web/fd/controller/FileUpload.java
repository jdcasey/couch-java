package org.commonjava.web.fd.controller;

import static org.apache.commons.io.IOUtils.closeQuietly;
import static org.apache.commons.io.IOUtils.copy;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.commonjava.util.logging.Logger;
import org.commonjava.web.fd.config.FileDepotConfiguration;
import org.richfaces.event.FileUploadEvent;
import org.richfaces.model.UploadedFile;

@RequestScoped
@ManagedBean( name = "fileUpload" )
public class FileUpload
{
    private final Logger log = new Logger( getClass() );

    @Inject
    @Named( "standalone" )
    private FileDepotConfiguration config;

    private String description;

    private File file;

    public void listener( final FileUploadEvent uploadEvent )
    {
        final UploadedFile f = uploadEvent.getUploadedFile();

        final File dest = new File( config.getUploadDir(), f.getName() );
        dest.mkdirs();

        log.info( "\n\n\nSaved: %s\nSize: %s\nTo: %s\n\n\n", f.getName(), f.getSize(), dest );

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
        }
        finally
        {
            closeQuietly( in );
            closeQuietly( out );
        }
    }

    public void save()
    {
        log.info( "\n\n\nSaved: %s\nDescription: '%s'\n\n\n", file.getAbsolutePath(), description );
    }

    public File getFile()
    {
        return file;
    }

    public void setFile( final File file )
    {
        log.info( "binding file: '%s'", file );
        this.file = file;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription( final String description )
    {
        log.info( "binding '%s'", description );
        this.description = description;
    }
}