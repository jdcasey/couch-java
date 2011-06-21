package org.commonjava.web.fd.config;

import java.io.File;
import java.util.Properties;

public interface FileDepotConfiguration
{

    File getUploadDir();

    Properties getTemplateEngineProperties();

    String getSecurityConfiguration();

    String getSecurityConfigurationIni();

    String getMailTemplate( String templateType, String defaultTemplate );

    String getMailSubject( String templateType, String defaultSubject );

    String getTemplateEncoding();

    String getMailFromAddress();

}