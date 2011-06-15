package org.commonjava.web.fd.config;

import javax.servlet.annotation.WebFilter;

import org.apache.shiro.web.servlet.IniShiroFilter;

@WebFilter( filterName = "shiro", urlPatterns = "/*" )
public class FDShiroFilter
    extends IniShiroFilter
{

}
