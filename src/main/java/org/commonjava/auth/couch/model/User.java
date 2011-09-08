/*******************************************************************************
 * Copyright (C) 2011  John Casey
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public
 * License along with this program.  If not, see 
 * <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package org.commonjava.auth.couch.model;

import static org.commonjava.auth.couch.util.IdUtils.namespaceId;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.commonjava.couch.model.AbstractCouchDocument;
import org.commonjava.couch.model.DenormalizedCouchDoc;

import com.google.gson.annotations.Expose;

public class User
    extends AbstractCouchDocument
    implements DenormalizedCouchDoc
{

    public static final String ADMIN = "admin";

    public static final String NAMESPACE = "user";

    public static final String NOT_SPECIFIED = "";

    private String username;

    private String passwordDigest;

    private String firstName;

    private String lastName;

    private String email;

    @Expose( deserialize = false )
    private final String doctype = NAMESPACE;

    private Set<String> roles;

    User()
    {}

    public User( final String username, final String email, final String firstName,
                 final String lastName, final String passwordDigest )
    {
        setUsername( username );
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.passwordDigest = passwordDigest;
        calculateDenormalizedFields();
    }

    public User( final String username, final Role... roles )
    {
        this.username = username;
        calculateDenormalizedFields();
        setRoles( new HashSet<Role>( Arrays.asList( roles ) ) );
    }

    public String getUsername()
    {
        return username;
    }

    void setUsername( final String username )
    {
        this.username = username;
    }

    public String getPasswordDigest()
    {
        return passwordDigest;
    }

    public void setPasswordDigest( final String passwordDigest )
    {
        this.passwordDigest = passwordDigest;
    }

    public String getFirstName()
    {
        return firstName;
    }

    public void setFirstName( final String firstName )
    {
        this.firstName = firstName;
    }

    public String getLastName()
    {
        return lastName;
    }

    public void setLastName( final String lastName )
    {
        this.lastName = lastName;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail( final String email )
    {
        this.email = email;
    }

    public Set<String> getRoles()
    {
        return roles;
    }

    public synchronized void setRoles( final Set<Role> roles )
    {
        if ( this.roles == null )
        {
            this.roles = new HashSet<String>();
        }
        else
        {
            this.roles.clear();
        }

        if ( roles != null )
        {
            for ( Role role : roles )
            {
                this.roles.add( role.getName() );
            }
        }
    }

    public synchronized void setRoleNames( final Set<String> roles )
    {
        if ( this.roles == null )
        {
            this.roles = new HashSet<String>();
        }
        else
        {
            this.roles.clear();
        }

        if ( roles != null )
        {
            for ( String role : roles )
            {
                this.roles.add( role );
            }
        }
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + username.hashCode();
        return result;
    }

    @Override
    public boolean equals( final Object obj )
    {
        if ( this == obj )
        {
            return true;
        }
        if ( obj == null )
        {
            return false;
        }
        if ( getClass() != obj.getClass() )
        {
            return false;
        }
        final User other = (User) obj;
        if ( !username.equals( other.username ) )
        {
            return false;
        }
        return true;
    }

    public boolean addRole( final Role role )
    {
        if ( role == null )
        {
            return false;
        }

        return addRole( role.getName() );
    }

    public synchronized boolean addRole( final String roleName )
    {
        if ( roles == null )
        {
            roles = new HashSet<String>();
        }

        return roles.add( roleName );
    }

    public boolean removeRole( final Role role )
    {
        if ( role == null )
        {
            return false;
        }

        return removeRole( role.getName() );
    }

    public boolean removeRole( final String roleName )
    {
        if ( roles != null )
        {
            return roles.remove( roleName );
        }

        return false;
    }

    public String getDoctype()
    {
        return doctype;
    }

    @Override
    public String toString()
    {
        return String.format( "User [\n  username=%s\n  passwordDigest=%s\n  firstName=%s\n  lastName=%s\n  email=%s\n  roles=%s]",
                              username, passwordDigest, firstName, lastName, email, roles );
    }

    @Override
    public void calculateDenormalizedFields()
    {
        setCouchDocId( namespaceId( NAMESPACE, this.username ) );
    }

}
