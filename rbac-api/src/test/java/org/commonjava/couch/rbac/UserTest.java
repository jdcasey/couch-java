package org.commonjava.couch.rbac;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class UserTest
{

    @Test
    public void gsonRoundTrip()
    {
        final User user =
            new User( "username", "email@nowhere.com", "first", "last",
                      "abcdefgabcdefgabcdefgabcdefgabcdefgabcdefgabcdefgabcdefgabcdefgabcdefgabcdefgabcdefg" );

        final Gson gson = new GsonBuilder().setPrettyPrinting()
                                           .create();

        final String json = gson.toJson( user );

        System.out.println( json );

        final User result = gson.fromJson( json, User.class );

        assertThat( result, equalTo( user ) );
    }

    @Test
    public void gsonRoundTripWithRole()
    {
        final User user =
            new User( "username", "email@nowhere.com", "first", "last",
                      "abcdefgabcdefgabcdefgabcdefgabcdefgabcdefgabcdefgabcdefgabcdefgabcdefgabcdefgabcdefg" );

        user.addRole( new Role( "role" ) );

        final Gson gson = new GsonBuilder().setPrettyPrinting()
                                           .create();

        final String json = gson.toJson( user );

        System.out.println( json );

        final User result = gson.fromJson( json, User.class );

        assertThat( result, equalTo( user ) );
    }

    @Test
    public void gsonRoundTripWithRemovedRole()
    {
        final User user =
            new User( "username", "email@nowhere.com", "first", "last",
                      "abcdefgabcdefgabcdefgabcdefgabcdefgabcdefgabcdefgabcdefgabcdefgabcdefgabcdefgabcdefg" );

        user.addRole( new Role( "role" ) );

        final Gson gson = new GsonBuilder().setPrettyPrinting()
                                           .create();

        String json = gson.toJson( user );

        System.out.println( json );

        User result = gson.fromJson( json, User.class );

        assertThat( result, equalTo( user ) );

        user.removeRole( "role" );

        json = gson.toJson( user );

        System.out.println( json );

        result = gson.fromJson( json, User.class );

        assertThat( result, equalTo( user ) );
    }

}
