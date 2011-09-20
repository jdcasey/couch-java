/*******************************************************************************
 * Copyright (C) 2011  John Casey
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public
 * License along with this program.  If not, see 
 * <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package org.commonjava.couch.util;

import org.commonjava.util.logging.Logger;

public class ChangeSynchronizer
{

    private final Logger logger = new Logger( getClass() );

    private boolean changed = false;

    public synchronized void setChanged()
    {
        changed = true;
        notifyAll();
    }

    public void resetChanged()
    {
        changed = false;
    }

    public synchronized void waitForChange( final long totalMillis, final long pollMillis )
    {
        long start = System.currentTimeMillis();
        double runningTotal = 0;

        while ( !changed )
        {
            runningTotal = ( System.currentTimeMillis() - start );
            logger.debug( "Waited (%s ms)...", runningTotal );

            if ( runningTotal > ( totalMillis ) )
            {
                logger.debug( "Wait (%s ms) expired.", totalMillis );
                break;
            }

            try
            {
                logger.debug( "Waiting (%s ms) for changes.", pollMillis );
                wait( pollMillis );
            }
            catch ( InterruptedException e )
            {
                break;
            }
        }

        if ( changed )
        {
            logger.debug( "Setting changed state to false." );
            changed = false;
        }

        logger.debug( "waitFoChange() exiting." );
    }

}
