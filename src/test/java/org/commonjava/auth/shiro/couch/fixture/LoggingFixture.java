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
package org.commonjava.auth.shiro.couch.fixture;

import java.net.URL;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Category;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;
import org.apache.log4j.spi.Configurator;
import org.apache.log4j.spi.LoggerRepository;

public class LoggingFixture
{

    public static void setupLogging( final Level level )
    {
        final Configurator log4jConfigurator = new Configurator()
        {
            @Override
            public void doConfigure( final URL notUsed, final LoggerRepository repo )
            {
                final ConsoleAppender cAppender = new ConsoleAppender( new SimpleLayout() );
                cAppender.setThreshold( level );

                repo.setThreshold( level );
                repo.getRootLogger().removeAllAppenders();
                repo.getRootLogger().setLevel( level );
                repo.getRootLogger().addAppender( cAppender );

                @SuppressWarnings( "unchecked" )
                List<Logger> loggers = Collections.list( repo.getCurrentLoggers() );

                for ( final Logger logger : loggers )
                {
                    logger.setLevel( level );
                }

                @SuppressWarnings( "unchecked" )
                List<Category> cats = Collections.list( repo.getCurrentCategories() );
                for ( final Category cat : cats )
                {
                    cat.setLevel( level );
                }
            }
        };

        log4jConfigurator.doConfigure( null, LogManager.getLoggerRepository() );
    }

}
