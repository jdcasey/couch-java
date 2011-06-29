package org.commonjava.web.fd.controller;

import java.io.Serializable;

import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Named;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

import org.commonjava.util.logging.Logger;

@Named
@SessionScoped
public class WorkspaceSession
    implements HttpSessionBindingListener, Serializable
{

    private static final long serialVersionUID = 1L;

    private final Logger logger = new Logger( getClass() );

    private Long currentWorkspace;

    @Produces
    @Named( "currentWorkspace" )
    public Long getCurrentWorkspace()
    {
        logger.info( "Retrieving current workspace: %s", currentWorkspace );
        return currentWorkspace;
    }

    public void setWorkspace( final Long workspace )
    {
        this.currentWorkspace = workspace;
        logger.info( "Set current workspace to: %s", workspace );
    }

    @Override
    public void valueBound( final HttpSessionBindingEvent event )
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void valueUnbound( final HttpSessionBindingEvent event )
    {
        // TODO Auto-generated method stub

    }
}
