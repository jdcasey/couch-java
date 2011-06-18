package org.commonjava.web.fd.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Workspace
{

    @Id
    @Column( nullable = false )
    @GeneratedValue( strategy = GenerationType.AUTO )
    private Long id;

    @Column( nullable = false, unique = true, length = 128 )
    private String name;

    @Column( nullable = false, unique = true, length = 128 )
    private String pathName;

    public Long getId()
    {
        return id;
    }

    public void setId( final Long id )
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName( final String name )
    {
        this.name = name;
    }

    @Override
    public String toString()
    {
        return String.format( "Workspace [id=%s, name='%s']", id, name );
    }

    public String getPathName()
    {
        return pathName;
    }

    public void setPathName( final String pathName )
    {
        this.pathName = pathName;
    }

}
