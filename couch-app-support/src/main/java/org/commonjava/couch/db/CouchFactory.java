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
package org.commonjava.couch.db;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;

import javax.enterprise.event.Event;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Inject;
import javax.inject.Qualifier;
import javax.inject.Singleton;

import org.commonjava.couch.change.CouchChangeListener;
import org.commonjava.couch.change.dispatch.CouchChangeDispatcher;
import org.commonjava.couch.change.j2ee.ApplicationEvent;
import org.commonjava.couch.change.j2ee.DatabaseEvent;
import org.commonjava.couch.conf.CouchDBConfiguration;
import org.commonjava.couch.io.CouchAppReader;
import org.commonjava.couch.io.CouchHttpClient;
import org.commonjava.couch.io.Serializer;

@Singleton
public class CouchFactory
{

    private static final Annotation[] PROTOTYPE = {};

    @Inject
    @Any
    private Instance<CouchDBConfiguration> config;

    @Inject
    private CouchAppReader appReader;

    @Inject
    private Event<DatabaseEvent> dbEvent;

    @Inject
    private Event<ApplicationEvent> appEvent;

    @Inject
    private Serializer serializer;

    @Inject
    private CouchChangeDispatcher dispatcher;

    public CouchChangeListener getChangeListener( final InjectionPoint injection )
    {
        return getChangeListener( getAnnotatedConfig( injection ) );
    }

    public CouchChangeListener getChangeListener( final CouchDBConfiguration configuration )
    {
        return new CouchChangeListener( dispatcher, getHttpClient( configuration ), configuration,
                                        getCouchManager( configuration ), serializer );
    }

    public CouchManager getCouchManager( final InjectionPoint injection )
    {
        return getCouchManager( getAnnotatedConfig( injection ) );
    }

    public CouchHttpClient getHttpClient( final InjectionPoint injection )
    {
        return getHttpClient( getAnnotatedConfig( injection ) );
    }

    public CouchHttpClient getHttpClient( final CouchDBConfiguration configuration )
    {
        return new CouchHttpClient( configuration, serializer );
    }

    public CouchManager getCouchManager( final CouchDBConfiguration configuration )
    {
        return new CouchManager( configuration, getHttpClient( configuration ), serializer,
                                 appReader, dbEvent, appEvent );
    }

    @SuppressWarnings( "serial" )
    private CouchDBConfiguration getAnnotatedConfig( final InjectionPoint injection )
    {
        Annotation[] qualifiers = getQualifiers( injection );

        CouchDBConfiguration configuration = config.select( qualifiers ).get();
        if ( configuration == null )
        {
            configuration = config.select( new AnnotationLiteral<Default>()
            {} ).get();
        }

        return configuration;
    }

    private Annotation[] getQualifiers( final InjectionPoint injection )
    {
        Set<Annotation> annotations = injection.getAnnotated().getAnnotations();
        Set<Annotation> result = new HashSet<Annotation>();
        for ( Annotation annotation : annotations )
        {
            Annotation[] annos = annotation.annotationType().getAnnotations();
            for ( Annotation anno : annos )
            {
                if ( anno instanceof Qualifier )
                {
                    result.add( annotation );
                    break;
                }
            }
        }

        return result.toArray( PROTOTYPE );
    }

}
