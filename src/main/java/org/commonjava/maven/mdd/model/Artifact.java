/*******************************************************************************
 * Copyright (C) 2011  John Casey
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package org.commonjava.maven.mdd.model;

import org.apache.maven.mae.project.key.FullProjectKey;
import org.apache.maven.model.Dependency;
import org.apache.maven.project.MavenProject;

public class Artifact
{

    private String groupId;

    private String artifactId;

    private String version;

    Artifact()
    {}

    public Artifact( final String groupId, final String artifactId, final String version )
    {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
    }

    public Artifact( final Dependency mavenDep )
    {
        this( mavenDep.getGroupId(), mavenDep.getArtifactId(), mavenDep.getVersion() );
    }

    public Artifact( final MavenProject project )
    {
        this( project.getGroupId(), project.getArtifactId(), project.getVersion() );
    }

    public Artifact( final FullProjectKey projectKey )
    {
        this( projectKey.getGroupId(), projectKey.getArtifactId(), projectKey.getVersion() );
    }

    public Artifact( final String key )
        throws InvalidKeyException
    {
        String[] parts = key.split( ":" );
        if ( parts.length != 3 )
        {
            throw new InvalidKeyException( "Invalid artifact key: '%s'", key );
        }

        this.groupId = parts[0];
        this.artifactId = parts[1];
        this.version = parts[2];
    }

    public String getGroupId()
    {
        return groupId;
    }

    void setGroupId( final String groupId )
    {
        this.groupId = groupId;
    }

    public String getArtifactId()
    {
        return artifactId;
    }

    void setArtifactId( final String artifactId )
    {
        this.artifactId = artifactId;
    }

    public String getVersion()
    {
        return version;
    }

    void setVersion( final String version )
    {
        this.version = version;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ( ( artifactId == null ) ? 0 : artifactId.hashCode() );
        result = prime * result + ( ( groupId == null ) ? 0 : groupId.hashCode() );
        result = prime * result + ( ( version == null ) ? 0 : version.hashCode() );
        return result;
    }

    @Override
    public boolean equals( final Object obj )
    {
        if ( this == obj )
        {
            return true;
        }
        if ( obj == null )
        {
            return false;
        }
        if ( getClass() != obj.getClass() )
        {
            return false;
        }
        Artifact other = (Artifact) obj;
        if ( artifactId == null )
        {
            if ( other.artifactId != null )
            {
                return false;
            }
        }
        else if ( !artifactId.equals( other.artifactId ) )
        {
            return false;
        }
        if ( groupId == null )
        {
            if ( other.groupId != null )
            {
                return false;
            }
        }
        else if ( !groupId.equals( other.groupId ) )
        {
            return false;
        }
        if ( version == null )
        {
            if ( other.version != null )
            {
                return false;
            }
        }
        else if ( !version.equals( other.version ) )
        {
            return false;
        }
        return true;
    }

    @Override
    public String toString()
    {
        return String.format( "%s:%s:%s", groupId, artifactId, version );
    }

}
