package org.commonjava.web.fd.config;

import static org.apache.commons.io.IOUtils.closeQuietly;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.Produces;

import org.commonjava.web.config.ConfigurationException;
import org.commonjava.web.config.ConfigurationListener;
import org.commonjava.web.config.ConfigurationSectionListener;
import org.commonjava.web.config.DefaultConfigurationRegistry;
import org.commonjava.web.config.dotconf.DotConfConfigurationReader;
import org.commonjava.web.config.section.BeanSectionListener;

@ApplicationScoped
public class FileDepotConfigurationFactory
    implements ConfigurationListener
{

    private static final String CONFIG_PATH = "/etc/file-depot/file-depot.conf";

    private final BeanSectionListener<StandaloneFileDepotConfiguration> FD_SECTION_LISTENER =
        new BeanSectionListener<StandaloneFileDepotConfiguration>( StandaloneFileDepotConfiguration.class );

    private StandaloneFileDepotConfiguration configuration;

    @PostConstruct
    protected void load()
        throws ConfigurationException
    {
        InputStream stream = null;
        try
        {
            stream = new FileInputStream( CONFIG_PATH );
            new DotConfConfigurationReader( new DefaultConfigurationRegistry( this ) ).loadConfiguration( stream );
        }
        catch ( final IOException e )
        {
            throw new ConfigurationException( "Cannot open configuration file: %s. Reason: %s", e, CONFIG_PATH,
                                              e.getMessage() );
        }
        finally
        {
            closeQuietly( stream );
        }
    }

    @Produces
    @Default
    public FileDepotConfiguration getConfiguration()
    {
        return configuration;
    }

    @Override
    public Map<String, ConfigurationSectionListener<?>> getSectionListeners()
    {
        final Map<String, ConfigurationSectionListener<?>> listeners =
            new HashMap<String, ConfigurationSectionListener<?>>();

        listeners.put( DotConfConfigurationReader.DEFAULT_SECTION, FD_SECTION_LISTENER );
        return listeners;
    }

    @Override
    public void configurationComplete()
        throws ConfigurationException
    {
        configuration = FD_SECTION_LISTENER.getConfiguration();
    }

}
