/*******************************************************************************
 * Copyright 2011 John Casey
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package org.commonjava.couch.model.io;

import java.io.IOException;

import org.commonjava.couch.db.CouchDBException;
import org.commonjava.couch.db.model.SimpleAppDescription;
import org.commonjava.couch.io.CouchAppReader;
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
