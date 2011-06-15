package org.commonjava.web.fd.config;

import javax.servlet.annotation.WebFilter;

import org.apache.shiro.web.filter.authc.BasicHttpAuthenticationFilter;

@WebFilter( filterName = "shiro-auth", urlPatterns = "/*" )
public class FDShiroAuthFilter
    extends BasicHttpAuthenticationFilter
{

}
