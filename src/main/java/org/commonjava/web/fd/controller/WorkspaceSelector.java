/*******************************************************************************
 * Copyright (C) 2011 John Casey.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
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
