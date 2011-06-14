package org.commonjava.web.fd.config;

import static org.apache.commons.io.IOUtils.closeQuietly;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

import org.commonjava.util.logging.Logger;

@Named( "standalone" )
@ApplicationScoped
public class StandaloneFileDepotConfiguration implements FileDepotConfiguration
{

    protected static final String CONFIG_PATH = "file-depot.properties";

    protected static final String KEY_UPLOAD_DIR = "upload.directory";

    protected static final String DEFAULT_UPLOAD_DIR = System.getProperty( "java.io.tmpdir", "/tmp" ) + "/uploads";

    private final Logger logger = new Logger( getClass() );

    private File uploadDir;

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
}
