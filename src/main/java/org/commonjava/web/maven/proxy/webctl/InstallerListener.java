package org.commonjava.web.maven.proxy.webctl;

import javax.inject.Inject;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.commonjava.web.maven.proxy.data.ProxyDataException;
import org.commonjava.web.maven.proxy.data.ProxyDataManager;
import org.commonjava.web.maven.proxy.model.Group;
import org.commonjava.web.maven.proxy.model.Repository;

@WebListener
public class InstallerListener
    implements ServletContextListener
{

    @Inject
    private ProxyDataManager dataManager;

    @Override
    public void contextInitialized( final ServletContextEvent sce )
    {
        try
        {
            dataManager.install();
            dataManager.storeRepository( new Repository( "central",
                                                         "http://repo1.maven.apache.org/maven2/" ),
                                         true );

            dataManager.storeGroup( new Group( "public", "central" ), true );
        }
        catch ( ProxyDataException e )
        {
            throw new RuntimeException( "Failed to install proxy database: " + e.getMessage(), e );
        }
    }

    @Override
    public void contextDestroyed( final ServletContextEvent sce )
    {
        // NOP
    }

}
