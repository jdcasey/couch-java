package org.commonjava.maven.mdd.model;

import com.google.gson.annotations.SerializedName;

public class DatabaseError
{

    @SerializedName( "reason" )
    private String error;

    public String getError()
    {
        return error;
    }

    void setError( final String error )
    {
        this.error = error;
    }

    @Override
    public String toString()
    {
        return error;
    }

}
