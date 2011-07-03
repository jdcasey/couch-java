package org.commonjava.web.fd.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Model;
import javax.enterprise.inject.Produces;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.inject.Named;

import org.commonjava.web.fd.data.WorkspaceDataManager;
import org.commonjava.web.fd.data.WorkspaceSession;
import org.commonjava.web.fd.model.Workspace;

@Model
public class WorkspaceSelector
{

    @Inject
    private WorkspaceSession session;

    @Inject
    private WorkspaceDataManager dataManager;

    private WorkspaceIdDTO selection;

    @SuppressWarnings( "unused" )
    @PostConstruct
    private void createWorkspaceSelection()
    {
        selection = new WorkspaceIdDTO( session.getCurrentWorkspaceId() );
    }

    @Produces
    @Named
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
    public WorkspaceIdDTO getCurrentWorkspace()
    {
        return selection;
    }

    public String select()
        throws IOException
    {
        session.setCurrentWorkspaceId( selection.getWorkspaceId() );
        if ( session.getCurrentWorkspaceId() != null )
        {
            FacesContext.getCurrentInstance()
                        .getExternalContext()
                        .redirect( "files.xhtml" );
        }

        return null;
    }

    public static final class WorkspaceIdDTO
    {
        private String workspaceId;

        public WorkspaceIdDTO( final Long currentWorkspaceId )
        {
            if ( currentWorkspaceId != null )
            {
                workspaceId = Long.toString( currentWorkspaceId );
            }
        }

        public String getId()
        {
            return workspaceId;
        }

        public Long getWorkspaceId()
        {
            return workspaceId == null ? null : Long.parseLong( workspaceId );
        }

        public void setId( final String workspaceId )
        {
            this.workspaceId = workspaceId;
        }
    }

}
