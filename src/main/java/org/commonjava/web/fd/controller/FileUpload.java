package org.commonjava.web.fd.controller;

import java.io.File;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import org.commonjava.util.logging.Logger;
import org.richfaces.event.FileUploadEvent;
import org.richfaces.model.UploadedFile;

@RequestScoped
@ManagedBean( name = "fileUpload" )
public class FileUpload
{
    private final Logger log = new Logger( getClass() );

    private String description;

    private File file;

    public void listener( final FileUploadEvent uploadEvent )
    {
        final UploadedFile f = uploadEvent.getUploadedFile();
        f.getInputStream();
        log.info( "\n\n\nSaved: " + f.getName() + "\nSize: " + f.getSize() + "\n\n\n" );
    }

    public void save()
    {
        log.info( "\n\n\nSaved: " + file.getAbsolutePath() + "\nDescription: '" + description + "'\n\n\n" );
    }

    public File getFile()
    {
        return file;
    }

    public void setFile( final File file )
    {
        log.info( "binding file: '" + file + "'" );
        this.file = file;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription( final String description )
    {
        log.info( "binding '" + description + "'" );
        this.description = description;
    }
}