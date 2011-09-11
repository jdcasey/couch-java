package org.commonjava.web.maven.proxy.conf;

import java.io.File;

public interface ProxyConfiguration
{

    String getDatabaseUrl();

    File getRepositoryRootDirectory();

}
