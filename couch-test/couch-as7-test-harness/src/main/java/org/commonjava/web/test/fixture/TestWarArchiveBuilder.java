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
package org.commonjava.web.test.fixture;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.commonjava.util.logging.Logger;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.UrlAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.impl.base.asset.ClassLoaderAsset;

public class TestWarArchiveBuilder
{

    private final Logger logger = new Logger( TestWarArchiveBuilder.class );

    protected WebArchive war;

    protected File testPom;

    private final File buildOutput;

    private final Set<String> excludedBuildOutput = new HashSet<String>();

    private static final Set<String> DEFAULT_LIB_FILTERS = new HashSet<String>()
    {
        private static final long serialVersionUID = 1L;

        {
            add( "jboss.+" );
            add( "arquillian.+" );
            add( "jbosgi.+" );
            add( "jsoup.+" );
            add( "cal10n.+" );
            add( "cdi-api.+" );
            add( "commons-httpclient.+" );
            add( "el-api.+" );
            add( "guava.+" );
            add( "httpserver.+" );
            add( "jandex.+" );
            add( "javassist.+" );
            add( "javax.inject.+" );
            add( "jaxrs-api.+" );
            add( "jcip-annotations.+" );
            add( "jsr250-api.+" );
            add( "shrinkwrap.+" );
            add( "staxmapper.+" );
            add( "tjws.+" );
            add( "weld.+" );
            add( "xnio.+" );
            add( "resteasy.+" );
            add( "scannotation.+" );
        }
    };

    private final Set<String> libraryFilters = new HashSet<String>()
    {
        private static final long serialVersionUID = 1L;

        {
            addAll( DEFAULT_LIB_FILTERS );
        }
    };

    private File librariesDir;

    private File knockoutRewritesDir = new File( "target/knockout-rewritten" );

    private final Map<String, JarKnockouts> knockouts = new HashMap<String, JarKnockouts>();

    public TestWarArchiveBuilder( final Class<?> testClass )
    {
        this( testClass, new File( "target/classes" ) );
    }

    public TestWarArchiveBuilder( final Class<?> testClass, final File buildOutput )
    {
        this.buildOutput = buildOutput.getAbsoluteFile();
        war = ShrinkWrap.create( WebArchive.class, "test.war" )
                        .addAsWebInfResource( new ClassLoaderAsset( "beans.xml.test" ), "beans.xml" )
                        .addAsWebInfResource( new ClassLoaderAsset( "test.web.xml" ), "web.xml" );

        war.addClass( testClass );
    }

    public TestWarArchiveBuilder withKnockoutRewritesDir( final File directory )
    {
        this.knockoutRewritesDir = directory;
        return this;
    }

    public TestWarArchiveBuilder withJarKnockoutPaths( final String jarNamePattern, final String... paths )
    {
        JarKnockouts jk = knockouts.get( jarNamePattern );
        if ( jk == null )
        {
            jk = new JarKnockouts();
        }

        jk.knockoutPaths( paths );

        return this;
    }

    public TestWarArchiveBuilder withJarKnockoutClasses( final String jarNamePattern, final Class<?>... classes )
    {
        JarKnockouts jk = knockouts.get( jarNamePattern );
        if ( jk == null )
        {
            jk = new JarKnockouts();
        }

        jk.knockoutClasses( classes );

        return this;
    }

    public TestWarArchiveBuilder withLibrariesIn( final File directory )
    {
        librariesDir = directory;

        return this;
    }

    public TestWarArchiveBuilder withoutLibrary( final String filePattern )
    {
        libraryFilters.add( filePattern );
        return this;
    }

    public TestWarArchiveBuilder withoutBuildClasses( final Class<?>... classes )
    {
        for ( final Class<?> cls : classes )
        {
            excludedBuildOutput.add( cls.getName()
                                        .replace( '.', '/' ) + ".class" );
        }

        return this;
    }

    public TestWarArchiveBuilder withoutBuildOutputPath( final String... paths )
    {
        for ( final String path : paths )
        {
            excludedBuildOutput.add( path );
        }

        return this;
    }

    public TestWarArchiveBuilder withExtraClasses( final Class<?>... classes )
    {
        for ( final Class<?> cls : classes )
        {
            war.addClass( cls );
        }

        return this;
    }

    public TestWarArchiveBuilder withLog4jProperties()
    {
        final ClassLoader cloader = Thread.currentThread()
                                          .getContextClassLoader();

        final URL resource = cloader.getResource( "log4j.properties" );
        if ( resource != null )
        {
            war.addAsWebInfResource( new UrlAsset( resource ), "classes/log4j.properties" );
        }
        return this;
    }

    public WebArchive build()
    {
        if ( librariesDir != null )
        {
            libs: for ( final File file : librariesDir.listFiles() )
            {
                File f = file;

                if ( f.isDirectory() )
                {
                    logger.info( "Adding classes from exploded library directory: %s", f );
                    addDirectoryClasses( f );
                }
                else
                {
                    final String fname = f.getName();

                    for ( final String pattern : libraryFilters )
                    {
                        if ( fname.matches( pattern ) )
                        {
                            continue libs;
                        }
                    }

                    final Set<JarKnockouts> jks = new HashSet<JarKnockouts>();
                    for ( final Map.Entry<String, JarKnockouts> entry : this.knockouts.entrySet() )
                    {
                        if ( fname.matches( entry.getKey() ) )
                        {
                            jks.add( entry.getValue() );
                        }
                    }

                    if ( !jks.isEmpty() )
                    {
                        try
                        {
                            f = JarKnockouts.rewriteJar( f, knockoutRewritesDir, jks );
                        }
                        catch ( final IOException e )
                        {
                            throw new RuntimeException( "Failed to rewrite jar: " + fname + " with knock-outs. Error: "
                                + e.getMessage(), e );
                        }
                    }

                    logger.info( "Adding library: %s", f );
                    war.addAsLibrary( f );
                }
            }
        }

        addDirectoryClasses( buildOutput );

        return war;
    }

    private void addDirectoryClasses( final File dir )
    {
        logger.info( "Scanning classes directory: '%s' (directory? %b)...", dir, dir.isDirectory() );

        final String prefix = dir.getAbsolutePath();
        final IOFileFilter filter = new ExcludesFileFilter( prefix, excludedBuildOutput );

        for ( final File f : FileUtils.listFiles( dir, filter, filter ) )
        {
            if ( f.isFile() )
            {
                if ( f.getName()
                      .endsWith( ".class" ) )
                {
                    war.addClass( classname( prefix, f ) );
                }
                else
                {
                    war.addAsWebInfResource( f, "classes/" + subpath( prefix, f ) );
                }
            }
        }
    }

    private static String classname( final String prefix, final File f )
    {
        String path = subpath( prefix, f );
        path = path.replace( '/', '.' );
        path = path.substring( 0, path.length() - ".class".length() );

        return path;
    }

    private static String subpath( final String trimPrefix, final File file )
    {
        String result = file.getAbsolutePath()
                            .replace( '\\', '/' );

        if ( result.length() > trimPrefix.length() && result.startsWith( trimPrefix ) )
        {
            result = result.substring( trimPrefix.length() );
            if ( result.startsWith( "/" ) && result.length() > 1 )
            {
                result = result.substring( 1 );
            }
        }

        return result;
    }

    public class ExcludesFileFilter
        implements IOFileFilter
    {

        private final String trimPrefix;

        private final Set<String> excludes;

        public ExcludesFileFilter( final String trimPrefix, final Set<String> excludedBuildOutput )
        {
            this.trimPrefix = trimPrefix;
            this.excludes = excludedBuildOutput;
        }

        @Override
        public boolean accept( final File file )
        {
            final String subpath = subpath( trimPrefix, file );
            for ( final String exclude : excludes )
            {
                if ( subpath.startsWith( exclude ) )
                {
                    return false;
                }
            }

            return true;
        }

        @Override
        public boolean accept( final File dir, final String name )
        {
            return accept( new File( dir, name ) );
        }

    }

}
