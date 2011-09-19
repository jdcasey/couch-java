package org.commonjava.auth.shiro.couch.web;

import javax.inject.Inject;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.commonjava.auth.shiro.couch.CouchRealm;

/**
 * NOTE: This class is NOT annotated with @WebListener ({@link WebListener}) because the application implementation
 * should have control over whether this is used! To use, subclass this class, and add the annotation to that.
 * 
 * @author jdcasey
 */
public class CouchShiroSetupListener
    implements ServletContextListener
{

    @Inject
    private CouchRealm realm;

    @Override
    public void contextInitialized( final ServletContextEvent sce )
    {
        if ( realm == null )
        {
            throw new RuntimeException(
                                        "Failed to initialize security. Realm has not been injected!" );
        }

        realm.setupSecurityManager();
    }

    @Override
    public void contextDestroyed( final ServletContextEvent sce )
    {
        // NOP
    }

}
