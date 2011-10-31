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
package org.commonjava.web.user.rest.live;

import javax.inject.Inject;

import org.commonjava.auth.couch.inject.UserData;
import org.commonjava.couch.db.CouchManager;
import org.commonjava.couch.user.web.test.AbstractUserRESTCouchTest;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.MavenImporter;

public abstract class AbstractRESTfulUserManagerTest
    extends AbstractUserRESTCouchTest
{

    @Inject
    @UserData
    private CouchManager couch;

    @Deployment
    public static WebArchive createTestWar()
    {
        WebArchive war = ShrinkWrap.create( WebArchive.class, "test.war" );
        war.as( MavenImporter.class ).loadEffectivePom( "pom.xml" ).importTestDependencies().importBuildOutput().importTestBuildOutput();

        // // TODO: This doesn't work, apparently cannot add a directory as a WEB-INF resource...may need to iterate
        // // through files.
        // addClasses( war, "target/classes" );
        // addClasses( war, "target/test-classes" );
        //
        return war;
    }

    // private static void addClasses( final WebArchive war, final String basepath )
    // {
    // File base = new File( basepath );
    // DirectoryScanner scanner = new DirectoryScanner();
    // scanner.setBasedir( base );
    // scanner.setIncludes( new String[] { "**/*" } );
    //
    // scanner.scan();
    // String[] included = scanner.getIncludedFiles();
    //
    // for ( String includedFile : included )
    // {
    // File f = new File( includedFile );
    // String path = f.getParent();
    //
    // f = new File( base, includedFile );
    // if ( !f.isDirectory() )
    // {
    // war.addAsWebInfResource( f, "classes/" + path );
    // }
    // }
    // }

    protected AbstractRESTfulUserManagerTest()
    {}

    @Override
    protected CouchManager getCouchManager()
    {
        return couch;
    }

}
