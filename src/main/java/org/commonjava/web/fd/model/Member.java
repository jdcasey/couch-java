package org.commonjava.web.fd.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

@Entity
@XmlRootElement
@Table( uniqueConstraints = @UniqueConstraint( columnNames = "email" ) )
public class Member
    implements Serializable
{
    /** Default value included to remove warning. Remove or modify at will. **/
    private static final long serialVersionUID = 1L;

    private Long id;

    private String firstName;

    private String lastName;

    private String email;

    private String phoneNumber;

    @Id
    @GeneratedValue
    public Long getId()
    {
        return id;
    }

    public void setId( final Long id )
    {
        this.id = id;
    }

    @NotNull
    @Size( min = 1, max = 25 )
    @Pattern( regexp = "[A-Za-z ]*", message = "must contain only letters and spaces" )
    public String getFirstName()
    {
        return firstName;
    }

    public void setFirstName( final String firstName )
    {
        this.firstName = firstName;
    }

    @NotNull
    @Size( min = 1, max = 25 )
    @Pattern( regexp = "[A-Za-z ]*", message = "must contain only letters and spaces" )
    public String getLastName()
    {
        return lastName;
    }

    public void setLastName( final String lastName )
    {
        this.lastName = lastName;
    }

    @NotNull
    @NotEmpty
    @Email
    public String getEmail()
    {
        return email;
    }

    public void setEmail( final String email )
    {
        this.email = email;
    }

    @NotNull
    @Size( min = 10, max = 12 )
    @Digits( fraction = 0, integer = 12 )
    @Column( name = "phone_number" )
    public String getPhoneNumber()
    {
        return phoneNumber;
    }

    public void setPhoneNumber( final String phoneNumber )
    {
        this.phoneNumber = phoneNumber;
    }
}