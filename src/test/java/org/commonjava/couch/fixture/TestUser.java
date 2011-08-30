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

}
