package org.commonjava.web.fd.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Produces;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

import org.commonjava.util.logging.Logger;
import org.commonjava.web.fd.data.WorkspaceDataManager;
import org.commonjava.web.fd.model.Workspace;

@Named
@SessionScoped
public class WorkspaceSession
    implements HttpSessionBindingListener, Serializable
{

    private static final long serialVersionUID = 1L;

    private final Logger logger = new Logger( getClass() );

    private Long currentWorkspace;

    @Inject
    private WorkspaceDataManager dataManager;

    public List<SelectItem> getWorkspaceItems()
    {
        final List<Workspace> workspaces = dataManager.getWorkspaces();
        final List<SelectItem> items = new ArrayList<SelectItem>( workspaces.size() );
        for ( final Workspace ws : workspaces )
        {
            items.add( new SelectItem( ws.getId(), ws.getName() ) );
        }

        return items;
    }

    @Produces
    @Named
    public Long getCurrentWorkspaceId()
    {
        logger.info( "Retrieving current workspace: %s", currentWorkspace );
        return currentWorkspace;
    }

    public void setWorkspaceId( final Long workspace )
    {
        this.currentWorkspace = workspace;
        logger.info( "Set current workspace to: %s", workspace );
    }

    public void selectCurrentWorkspace()
    {
        logger.info( "Current workspace set to: %s", currentWorkspace );
    }

    @Override
    public void valueBound( final HttpSessionBindingEvent event )
    {
        logger.info( "Value bound to WorkspaceSession: %s", event.getValue() );
    }

    @Override
    public void valueUnbound( final HttpSessionBindingEvent event )
    {
        logger.info( "Value unbound from WorkspaceSession: %s", event.getValue() );
    }
}
