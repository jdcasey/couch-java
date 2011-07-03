package org.commonjava.web.fd.data;

import java.io.Serializable;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

@Named
@SessionScoped
public class WorkspaceSession
    implements Serializable
{

    private static final long serialVersionUID = 1L;

    private Long workspaceId;

    public Long getCurrentWorkspaceId()
    {
        return workspaceId;
    }

    public void setCurrentWorkspaceId( final Long workspaceId )
    {
        this.workspaceId = workspaceId;
    }

}
