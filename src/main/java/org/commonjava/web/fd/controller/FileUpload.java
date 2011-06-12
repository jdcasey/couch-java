package org.commonjava.web.fd.controller;

import java.io.File;

import javax.enterprise.inject.Model;

@Model
public class FileUpload
{

    // private final Logger log = new Logger( getClass() );

    private String description;

    private File file;

    public void save()
    {
        System.out.println( "\n\n\nSaved: " + file.getAbsolutePath() + "\nDescription: '" + description + "'\n\n\n" );
    }

    public File getFile()
    {
        return file;
    }

    public void setFile( final File file )
    {
        System.out.println( "binding file: '" + file + "'" );
        this.file = file;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription( final String description )
    {
        System.out.println( "binding '" + description + "'" );
        this.description = description;
    }
}