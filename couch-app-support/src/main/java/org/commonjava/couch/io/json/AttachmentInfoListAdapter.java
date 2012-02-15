package org.commonjava.couch.io.json;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.commonjava.couch.model.AttachmentInfo;
import org.commonjava.web.json.ser.WebSerializationAdapter;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

public class AttachmentInfoListAdapter
    implements WebSerializationAdapter, JsonDeserializer<List<AttachmentInfo>>
{

    @Override
    public List<AttachmentInfo> deserialize( final JsonElement json, final Type typeOfT,
                                             final JsonDeserializationContext context )
        throws JsonParseException
    {
        final JsonObject obj = json.getAsJsonObject();

        final List<AttachmentInfo> attachments = new ArrayList<AttachmentInfo>();
        for ( final Map.Entry<String, JsonElement> entry : obj.entrySet() )
        {
            final String name = entry.getKey();
            final JsonObject attObj = entry.getValue()
                                           .getAsJsonObject();
            JsonElement prop = attObj.get( "content_type" );

            final String contentType = prop == null ? null : prop.getAsString();

            prop = attObj.get( "length" );
            final int contentLength = prop == null ? -1 : prop.getAsInt();

            final AttachmentInfo info = new AttachmentInfo( name, contentType, contentLength );
            attachments.add( info );
        }

        return attachments;
    }

    @Override
    public void register( final GsonBuilder gsonBuilder )
    {
        gsonBuilder.registerTypeAdapter( new TypeToken<List<AttachmentInfo>>()
        {
        }.getType(), this );
    }

}
