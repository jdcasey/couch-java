package org.commonjava.web.fd.model;

import java.io.File;
import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class FileInfo
{

    private String name;

    private Long size;

    private Date lastModified;

    public FileInfo()
    {
    }

    public FileInfo( final File file )
    {
        name = file.getName();
        size = file.length();
        lastModified = new Date( file.lastModified() );
    }

    public String getName()
    {
        return name;
    }

    public Long getSize()
    {
        return size;
    }

    public Date getLastModified()
    {
        return lastModified;
    }

    public void setName( final String name )
    {
        this.name = name;
    }

    public void setSize( final Long size )
    {
        this.size = size;
    }

    public void setLastModified( final Date lastModified )
    {
        this.lastModified = lastModified;
    }

    @Override
    public String toString()
    {
        return String.format( "name=%s\nsize=%s\nlastModified=%s", name, size, lastModified );
    }

    public Long length()
    {
        return getSize();
    }

    public Long lastModified()
    {
        return getLastModified().getTime();
    }

}
