package org.commonjava.web.fd.config;

import java.io.File;

//@Named
//@ApplicationScoped
class StandaloneFileDepotConfiguration
    implements FileDepotConfiguration
{

    protected static final File DEFAULT_SECURITY_INI_PATH = new File( "/etc/file-depot/security.conf" );

    protected static final String KEY_UPLOAD_DIR = "upload.directory";

    protected static final File DEFAULT_UPLOAD_DIR = new File( System.getProperty( "java.io.tmpdir", "/tmp" ),
                                                               "uploads" );

    private File uploadDirectory = DEFAULT_UPLOAD_DIR;

    private File securityConfigurationFile = DEFAULT_SECURITY_INI_PATH;

    @Override
    public File getUploadDir()
    {
        return uploadDirectory;
    }

    @Override
    public File getSecurityConfigurationFile()
    {
        return securityConfigurationFile;
    }

    protected void setUploadDir( final File uploadDir )
    {
        this.uploadDirectory = uploadDir;
    }

    protected void setSecurityConfigurationFile( final File securityConfigurationFile )
    {
        this.securityConfigurationFile = securityConfigurationFile;
    }

}
