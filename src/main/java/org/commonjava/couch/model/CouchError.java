package org.commonjava.couch.model;

import com.google.gson.annotations.SerializedName;

public class CouchError
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
