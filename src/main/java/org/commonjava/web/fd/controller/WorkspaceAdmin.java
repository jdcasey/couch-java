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

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Model;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;

import org.commonjava.util.logging.Logger;
import org.commonjava.web.fd.data.WorkspaceDataException;
import org.commonjava.web.fd.data.WorkspaceDataManager;
import org.commonjava.web.fd.model.Workspace;
import org.commonjava.web.user.data.UserDataException;

@Model
public class WorkspaceAdmin
{

    private final Logger logger = new Logger( getClass() );

    private static final int GENERATED_PATH_MAXLEN = 12;

    @Inject
    private WorkspaceDataManager workspaceDataManager;

    private Workspace newWorkspace;

    @Produces
    @Named
    public Workspace getNewWorkspace()
    {
        return newWorkspace;
    }

    public void createWorkspace()
        throws WorkspaceDataException, UserDataException
    {
        logger.info( "\n\nSaving workspace: %s\n\n", newWorkspace );

        workspaceDataManager.saveWorkspace( newWorkspace, true );
        createWorkspaceInstance();
    }

    public void generatePathName()
    {
        if ( newWorkspace.getName() != null )
        {
            final StringBuilder sb = new StringBuilder();
            int count = 0;
            for ( final Character c : newWorkspace.getName()
                                                  .toCharArray() )
            {
                if ( count == GENERATED_PATH_MAXLEN )
                {
                    break;
                }
                else if ( !Character.isLetterOrDigit( c ) )
                {
                    sb.append( '-' );
                }
                else if ( Character.isUpperCase( c ) )
                {
                    if ( sb.length() > 0 )
                    {
                        sb.append( '-' );
                    }

                    sb.append( Character.toLowerCase( c ) );
                }
                else
                {
                    sb.append( c );
                }

                count++;
            }

            newWorkspace.setPathName( sb.toString() );
        }
    }

    @PostConstruct
    private void createWorkspaceInstance()
    {
        newWorkspace = new Workspace();
    }

}
