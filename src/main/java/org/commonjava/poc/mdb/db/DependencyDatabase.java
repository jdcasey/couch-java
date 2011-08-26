package org.commonjava.poc.mdb.db;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.maven.model.Dependency;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;
import org.commonjava.poc.mdb.model.DependencyRelationship;
import org.commonjava.poc.mdb.model.io.Serializer;
import org.commonjava.poc.mdb.session.DependencyDBSession;

@Component( role = DependencyDatabase.class )
public class DependencyDatabase
{

    private HttpClient client;

    @Requirement
    private Serializer serializer;

    protected synchronized HttpClient getClient()
    {
        if ( client == null )
        {
            client = new DefaultHttpClient();
        }
        return client;
    }

    public void validateConnection( final DependencyDBSession session )
        throws DatabaseException
    {
        HttpHead head = new HttpHead( session.getBaseUrl() );
        try
        {
            getClient().execute( head );
        }
        catch ( ClientProtocolException e )
        {
            throw new DatabaseException(
                                         "Failed to validate connection to dependency db: %s.\nReason: %s",
                                         e, session.getBaseUrl(), e.getMessage() );
        }
        catch ( IOException e )
        {
            throw new DatabaseException(
                                         "Failed to validate connection to dependency db: %s.\nReason: %s",
                                         e, session.getBaseUrl(), e.getMessage() );
        }
    }

    public void storeDependencies( final MavenProject project, final DependencyDBSession session )
        throws DatabaseException
    {
        for ( Dependency dep : project.getDependencies() )
        {
            DependencyRelationship rel = new DependencyRelationship( dep, project );
            if ( hasDependency( rel, session ) )
            {
                continue;
            }

            HttpPost post = new HttpPost( session.getBaseUrl() );
            try
            {
                post.setEntity( new StringEntity( serializer.toString( rel ), "application/json",
                                                  "UTF-8" ) );

                HttpResponse response = getClient().execute( post );
                StatusLine statusLine = response.getStatusLine();
                if ( statusLine.getStatusCode() != HttpStatus.SC_CREATED )
                {
                    throw new DatabaseException(
                                                 "Failed to store dependency relationship: %s.\nHTTP Response: %s",
                                                 rel, statusLine );
                }
            }
            catch ( UnsupportedEncodingException e )
            {
                throw new DatabaseException(
                                             "Failed to set HTTP entity for POST to store dependency relationship: %s.\nReason: %s",
                                             e, rel, e.getMessage() );
            }
            catch ( ClientProtocolException e )
            {
                throw new DatabaseException(
                                             "Failed to store dependency relationship: %s.\nReason: %s",
                                             e, rel, e.getMessage() );
            }
            catch ( IOException e )
            {
                throw new DatabaseException(
                                             "Failed to store dependency relationship: %s.\nReason: %s",
                                             e, rel, e.getMessage() );
            }
        }
    }

    public void deleteDependencies( final MavenProject project, final DependencyDBSession session )
        throws DatabaseException
    {
        for ( Dependency dep : project.getDependencies() )
        {
            DependencyRelationship rel = new DependencyRelationship( dep, project );
            if ( !hasDependency( rel, session ) )
            {
                continue;
            }

            String url = buildDocUrl( rel, true, session );
            HttpDelete post = new HttpDelete( url );
            try
            {
                HttpResponse response = getClient().execute( post );
                StatusLine statusLine = response.getStatusLine();
                if ( statusLine.getStatusCode() != HttpStatus.SC_OK )
                {
                    throw new DatabaseException(
                                                 "Failed to delete dependency relationship: %s.\nHTTP Response: %s\nURL: %s",
                                                 rel, statusLine, url );
                }
            }
            catch ( ClientProtocolException e )
            {
                throw new DatabaseException(
                                             "Failed to delete dependency relationship: %s.\nReason: %s\nURL: %s",
                                             e, rel, e.getMessage(), url );
            }
            catch ( IOException e )
            {
                throw new DatabaseException(
                                             "Failed to delete dependency relationship: %s.\nReason: %s\nURL: %s",
                                             e, rel, e.getMessage(), url );
            }
        }
    }

    private boolean hasDependency( final DependencyRelationship rel,
                                   final DependencyDBSession session )
        throws DatabaseException
    {
        String url = buildDocUrl( rel, false, session );

        boolean exists = false;

        HttpHead head = new HttpHead( url );
        try
        {
            HttpResponse response = getClient().execute( head );
            StatusLine statusLine = response.getStatusLine();
            exists = statusLine.getStatusCode() == HttpStatus.SC_OK;

            if ( exists )
            {
                Header etag = response.getFirstHeader( "Etag" );
                String rev = etag.getValue();
                if ( rev.startsWith( "\"" ) || rev.startsWith( "'" ) )
                {
                    rev = rev.substring( 1 );
                }

                if ( rev.endsWith( "\"" ) || rev.endsWith( "'" ) )
                {
                    rev = rev.substring( 0, rev.length() - 1 );
                }

                rel.setRev( rev );
            }
        }
        catch ( ClientProtocolException e )
        {
            throw new DatabaseException(
                                         "Failed to check for existence of dependency relationship: %s.\nReason: %s",
                                         e, rel, e.getMessage() );
        }
        catch ( IOException e )
        {
            throw new DatabaseException(
                                         "Failed to check for existence of dependency relationship: %s.\nReason: %s",
                                         e, rel, e.getMessage() );
        }

        return exists;
    }

    private String buildDocUrl( final DependencyRelationship rel, final boolean includeRevision,
                                final DependencyDBSession session )
        throws DatabaseException
    {
        StringBuilder urlBuilder = new StringBuilder( session.getBaseUrl() );
        if ( !session.getBaseUrl().endsWith( "/" ) )
        {
            urlBuilder.append( "/" );
        }

        urlBuilder.append( rel.getId() );

        if ( includeRevision && rel.getRev() != null )
        {
            urlBuilder.append( "?rev=" ).append( rel.getRev() );
        }

        try
        {
            return new URL( urlBuilder.toString() ).toExternalForm();
        }
        catch ( MalformedURLException e )
        {
            throw new DatabaseException( "Failed to format document URL for: %s. Reason: %s", e,
                                         rel, e.getMessage() );
        }
    }

}
