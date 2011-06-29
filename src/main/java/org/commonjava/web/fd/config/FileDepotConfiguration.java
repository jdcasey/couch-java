package org.commonjava.web.fd.config;

import java.io.File;

public interface FileDepotConfiguration
{

    File getUploadDirectory();

    File getSecurityConfigurationFile();

}