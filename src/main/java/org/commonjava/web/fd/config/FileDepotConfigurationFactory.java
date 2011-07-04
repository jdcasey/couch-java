/*******************************************************************************
 * Copyright (C) 2011 John Casey.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
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
import org.commonjava.web.config.dotconf.DotConfConfigurationReader;
import org.commonjava.web.config.section.BeanSectionListener;
import org.commonjava.web.config.section.ConfigurationSectionListener;
import org.commonjava.web.user.conf.DefaultUserManagerConfig;

@ApplicationScoped
public class FileDepotConfigurationFactory
    implements ConfigurationListener
{

    private static final String CONFIG_PATH = "/etc/file-depot/file-depot.conf";

    private final BeanSectionListener<DefaultFileDepotConfiguration> FD_SECTION_LISTENER =
        new BeanSectionListener<DefaultFileDepotConfiguration>( DefaultFileDepotConfiguration.class );

    private DefaultFileDepotConfiguration configuration;

    // @Inject
    // private PostOfficeConfigurationListener postOfficeConfigurationListener;

    @PostConstruct
    protected void load()
        throws ConfigurationException
    {
        InputStream stream = null;
        try
        {
            stream = new FileInputStream( CONFIG_PATH );
            new DotConfConfigurationReader( DefaultFileDepotConfiguration.class, DefaultUserManagerConfig.class/*, postOfficeConfigurationListener
                                                                                                                */).loadConfiguration( stream );
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

        listeners.put( ConfigurationSectionListener.DEFAULT_SECTION, FD_SECTION_LISTENER );
        return listeners;
    }

    @Override
    public void configurationComplete()
        throws ConfigurationException
    {
        configuration = FD_SECTION_LISTENER.getConfiguration();
    }

}
