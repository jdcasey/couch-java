package org.commonjava.web.fd.config;

import static org.apache.commons.io.IOUtils.closeQuietly;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.apache.velocity.runtime.resource.loader.FileResourceLoader;
import org.commonjava.util.logging.Logger;

@Named( "standalone" )
@ApplicationScoped
public class StandaloneFileDepotConfiguration
    implements FileDepotConfiguration
{

    protected static final String CONFIG_PATH = "/etc/file-depot/file-depot.properties";

    protected static final String TEMPLATE_PROPERTIES_PATH = "/etc/file-depot/template-engine.properties";

    protected static final String SECURITY_INI_PATH = "/etc/file-depot/security.ini";

    protected static final String KEY_UPLOAD_DIR = "upload.directory";

    protected static final String KEY_MAIL_FROM_ADDRESS = "mail.from";

    protected static final String DEFAULT_UPLOAD_DIR = System.getProperty( "java.io.tmpdir", "/tmp" ) + "/uploads";

    private final Logger logger = new Logger( getClass() );

    private File uploadDir;

    private String mailFromAddress;

    /**
     * @see org.commonjava.web.fd.config.FileDepotConfiguration#getUploadDir()
     */
    @Override
    public File getUploadDir()
    {
        return uploadDir;
    }

    @PostConstruct
    protected void loadConfiguration()
    {
        final Properties props = new Properties();

        final InputStream in = Thread.currentThread()
                                     .getContextClassLoader()
                                     .getResourceAsStream( CONFIG_PATH );
        if ( in == null )
        {
            logger.warn( "Cannot find classpath resource: %s", CONFIG_PATH );
        }
        else
        {
            try
            {
                props.load( in );
            }
            catch ( final IOException e )
            {
                logger.error( "Failed to load file-depot configuration from: %s. Reason: %s", e, CONFIG_PATH,
                              e.getMessage() );
            }
            finally
            {
                closeQuietly( in );
            }
        }

        uploadDir = new File( props.getProperty( KEY_UPLOAD_DIR, DEFAULT_UPLOAD_DIR ) );
        mailFromAddress = props.getProperty( KEY_MAIL_FROM_ADDRESS );

        if ( ( !uploadDir.exists() || !uploadDir.isDirectory() ) && !uploadDir.mkdirs() )
        {
            logger.error( "Invalid file-depot configuration at: %s. Cannot create upload directory: %s", CONFIG_PATH,
                          uploadDir.getAbsolutePath() );
        }
        else if ( !uploadDir.canWrite() )
        {
            logger.error( "Invalid file-depot configuration at: %s. Cannot write to upload directory: %s", CONFIG_PATH,
                          uploadDir.getAbsolutePath() );
        }
    }

    @Override
    public Properties getTemplateEngineProperties()
    {
        // TODO: Make this configurable
        final Properties props = new Properties();
        props.setProperty( RuntimeConstants.RESOURCE_LOADER, "file, class" );
        props.setProperty( "file.resource.loader.path", "/etc/file-depot/mail-templates" );
        props.setProperty( "file.resource.loader.cache", Boolean.toString( Boolean.TRUE ) );
        props.setProperty( "file.resource.loader.class", FileResourceLoader.class.getName() );

        props.setProperty( "class.resource.loader.class", ClasspathResourceLoader.class.getName() );

        return props;
    }

    @Override
    public String getSecurityConfiguration()
    {
        // TODO: Make this configurable
        return null;
    }

    @Override
    public String getSecurityConfigurationIni()
    {
        // TODO: Make this configurable
        return SECURITY_INI_PATH;
    }

    @Override
    public String getMailTemplate( final String templateType, final String defaultTemplate )
    {
        // TODO: Flesh this out to allow a mapping to custom email templates.
        return defaultTemplate;
    }

    @Override
    public String getMailSubject( final String templateType, final String defaultSubject )
    {
        // TODO: Flesh this out to allow a mapping to custom email templates.
        return defaultSubject;
    }

    @Override
    public String getTemplateEncoding()
    {
        // TODO: Flesh this out to allow custom encoding.
        return "ISO-8859-1";
    }

    @Override
    public String getMailFromAddress()
    {
        return mailFromAddress;
    }
}
