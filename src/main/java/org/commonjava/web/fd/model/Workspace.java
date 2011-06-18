package org.commonjava.web.fd.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.validator.constraints.NotBlank;

@Entity
@Table( name = "workspaces" )
public class Workspace
{

    @Id
    @Column( nullable = false, unique = true, length = 50 )
    @NotBlank
    private String id;

    @Column( nullable = false, unique = true, length = 128 )
    @NotBlank
    private String name;

    public String getId()
    {
        return id;
    }

    public void setId( final String id )
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

}
