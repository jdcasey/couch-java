/*******************************************************************************
 * Copyright (C) 2011  John Casey
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package org.commonjava.maven.mdd.db;

import java.util.List;

import org.commonjava.maven.mdd.MavenDependencyDBException;

public class DatabaseException
    extends MavenDependencyDBException
{

    private static final long serialVersionUID = 1L;

    private List<Throwable> nested;

    public DatabaseException( final String message, final Throwable error, final Object... params )
    {
        super( message, error, params );
    }

    public DatabaseException( final String message, final Object... params )
    {
        super( message, params );
    }

    public DatabaseException( final String message, final List<Throwable> nested,
                              final Object... params )
    {
        super( message, params );
        this.nested = nested;
    }

    @Override
    public String getMessage()
    {
        StringBuilder msg = new StringBuilder( super.getMessage() );
        if ( nested != null && !nested.isEmpty() )
        {
            msg.append( "\nNested errors:\n" );

            int idx = 1;
            for ( Throwable error : nested )
            {
                msg.append( "\n" ).append( idx ).append( ".  " ).append( error.getMessage() );
                idx++;
            }
        }

        return msg.toString();
    }
}
