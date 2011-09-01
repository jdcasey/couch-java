package org.commonjava.auth.couch.model.factory;

import org.commonjava.auth.couch.model.Permission;
import org.commonjava.couch.model.io.SerializationAdapter;

import com.google.gson.InstanceCreator;

public interface PermissionCreator<P extends Permission>
    extends InstanceCreator<P>, SerializationAdapter
{}
