/*******************************************************************************
 * Copyright 2011 John Casey
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package org.commonjava.auth.couch.model;

import static org.commonjava.auth.couch.model.MetadataKeys.ID_METADATA;
import static org.commonjava.auth.couch.model.MetadataKeys.REV_METADATA;
import static org.commonjava.couch.util.IdUtils.namespaceId;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.commonjava.couch.model.AbstractCouchDocument;
import org.commonjava.couch.model.DenormalizedCouchDoc;
import org.commonjava.couch.rbac.Role;
import org.commonjava.couch.rbac.User;

import com.google.gson.annotations.Expose;

public class UserDoc
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

    UserDoc()
    {
    }

    public UserDoc( final String username, final String email, final String firstName, final String lastName,
                    final String passwordDigest )
    {
        setUsername( username );
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.passwordDigest = passwordDigest;
        calculateDenormalizedFields();
    }

    public UserDoc( final String username, final Role... roles )
    {
        this.username = username;
        calculateDenormalizedFields();
        setRoles( new HashSet<Role>( Arrays.asList( roles ) ) );
    }

    public UserDoc( final User user )
    {
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.passwordDigest = user.getPasswordDigest();
        this.roles = user.getRoles();
        setCouchDocRev( user.getMetadata( REV_METADATA, String.class ) );
        calculateDenormalizedFields();
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
            for ( final Role role : roles )
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
            for ( final String role : roles )
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
        final UserDoc other = (UserDoc) obj;
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

    public static Collection<UserDoc> toDocuments( final Collection<User> users )
    {
        final Set<UserDoc> docs = new HashSet<UserDoc>();
        for ( final User user : users )
        {
            docs.add( new UserDoc( user ) );
        }
        return docs;
    }

    public static Set<User> toUserSet( final List<UserDoc> docs )
    {
        final Set<User> users = new HashSet<User>();
        for ( final UserDoc doc : docs )
        {
            users.add( doc.toUser() );
        }
        return users;
    }

    public User toUser()
    {
        final User u = new User( username, email, firstName, lastName, passwordDigest );
        u.setRoleNames( roles );
        u.setMetadata( REV_METADATA, getCouchDocRev() );
        u.setMetadata( ID_METADATA, getCouchDocId() );

        return u;
    }

}
