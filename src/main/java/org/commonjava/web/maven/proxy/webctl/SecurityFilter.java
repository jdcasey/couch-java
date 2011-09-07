package org.commonjava.web.maven.proxy.webctl;

import javax.servlet.annotation.WebFilter;

import org.commonjava.auth.shiro.couch.web.CouchShiroFilter;

@WebFilter( urlPatterns = "/*", filterName = "security" )
public class SecurityFilter
    extends CouchShiroFilter
{

}
