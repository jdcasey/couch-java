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
package org.commonjava.couch.model.io;

import java.io.IOException;

import org.commonjava.couch.db.CouchDBException;
import org.commonjava.couch.db.model.SimpleAppDescription;
import org.commonjava.couch.model.CouchApp;
import org.junit.Test;

public class CouchAppReaderTest
{

    @Test
    public void readTestApp()
        throws IOException, CouchDBException
    {
        CouchApp app =
            new CouchAppReader().readAppDefinition( new SimpleAppDescription( "test-app" ) );

        System.out.println( app );
    }

}
