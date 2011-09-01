package org.commonjava.auth.couch.model.factory;

import org.commonjava.auth.couch.model.Role;
import org.commonjava.couch.model.io.SerializationAdapter;

import com.google.gson.InstanceCreator;

public interface RoleCreator<R extends Role>
    extends InstanceCreator<R>, SerializationAdapter
{

}
