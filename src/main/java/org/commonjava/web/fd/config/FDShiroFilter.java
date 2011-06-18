package org.commonjava.web.fd.config;

import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebInitParam;

import org.apache.shiro.web.servlet.IniShiroFilter;

@WebFilter( filterName = "shiro", urlPatterns = "/*", initParams = @WebInitParam( name = "configPath", value = "/etc/shiro/shiro.ini" ) )
public class FDShiroFilter
    extends IniShiroFilter
{

}
