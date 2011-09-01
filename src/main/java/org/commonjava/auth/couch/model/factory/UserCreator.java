package org.commonjava.auth.couch.model.factory;

import org.commonjava.auth.couch.model.User;
import org.commonjava.couch.model.io.SerializationAdapter;

import com.google.gson.InstanceCreator;

public interface UserCreator<U extends User>
    extends InstanceCreator<U>, SerializationAdapter
{

}
