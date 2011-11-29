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
package org.commonjava.couch.fixture;

import org.commonjava.couch.model.AbstractCouchDocument;

public class TestUser
    extends AbstractCouchDocument
{

    private String username;

    private String first;

    private String last;

    private String email;

    public TestUser( final String username, final String first, final String last,
                     final String email )
    {
        this.username = username;
        this.first = first;
        this.last = last;
        this.email = email;
        setCouchDocId( username );
    }

    TestUser()
    {}

    public String getUsername()
    {
        return username;
    }

    public void setUsername( final String username )
    {
        this.username = username;
    }

    public String getFirst()
    {
        return first;
    }

    public void setFirst( final String first )
    {
        this.first = first;
    }

    public String getLast()
    {
        return last;
    }

    public void setLast( final String last )
    {
        this.last = last;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail( final String email )
    {
        this.email = email;
    }

    @Override
    public String toString()
    {
        return String.format( "TestUser [_id=%s, _rev=%s, username=%s, first=%s, last=%s, email=%s]",
                              getCouchDocId(), getCouchDocRev(), username, first, last, email );
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ( ( email == null ) ? 0 : email.hashCode() );
        result = prime * result + ( ( first == null ) ? 0 : first.hashCode() );
        result = prime * result + ( ( last == null ) ? 0 : last.hashCode() );
        result = prime * result + ( ( username == null ) ? 0 : username.hashCode() );
        return result;
    }

    @Override
    public boolean equals( final Object obj )
    {
        if ( this == obj )
        {
            return true;
        }
        if ( !super.equals( obj ) )
        {
            return false;
        }
        if ( getClass() != obj.getClass() )
        {
            return false;
        }
        TestUser other = (TestUser) obj;
        if ( email == null )
        {
            if ( other.email != null )
            {
                return false;
            }
        }
        else if ( !email.equals( other.email ) )
        {
            return false;
        }
        if ( first == null )
        {
            if ( other.first != null )
            {
                return false;
            }
        }
        else if ( !first.equals( other.first ) )
        {
            return false;
        }
        if ( last == null )
        {
            if ( other.last != null )
            {
                return false;
            }
        }
        else if ( !last.equals( other.last ) )
        {
            return false;
        }
        if ( username == null )
        {
            if ( other.username != null )
            {
                return false;
            }
        }
        else if ( !username.equals( other.username ) )
        {
            return false;
        }
        return true;
    }

}
