package org.commonjava.web.fd.config;

import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.servlet.annotation.WebFilter;

import org.apache.shiro.web.servlet.IniShiroFilter;

@WebFilter( filterName = "shiro", urlPatterns = "/*" )
public class FDShiroFilter
    extends IniShiroFilter
{

    @Inject
    @Default
    private FileDepotConfiguration config;

    @Override
    public String getConfigPath()
    {
        return config.getSecurityConfigurationFile()
                     .getAbsolutePath();
    }

}
