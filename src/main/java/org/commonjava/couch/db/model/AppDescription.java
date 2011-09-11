package org.commonjava.couch.db.model;

import java.util.Set;

public interface AppDescription
{

    String getAppName();

    String getClasspathAppResource();

    Set<String> getViewNames();

}
