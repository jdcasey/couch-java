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
