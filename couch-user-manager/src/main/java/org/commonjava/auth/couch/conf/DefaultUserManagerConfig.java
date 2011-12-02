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
package org.commonjava.auth.couch.conf;

import javax.enterprise.inject.Alternative;
import javax.inject.Named;

import org.commonjava.auth.couch.data.PasswordManager;
import org.commonjava.couch.conf.CouchDBConfiguration;
import org.commonjava.couch.conf.DefaultCouchDBConfiguration;
import org.commonjava.couch.rbac.User;
import org.commonjava.web.config.annotation.ConfigName;
import org.commonjava.web.config.annotation.SectionName;

@SectionName( "user-manager" )
@Named( "do-not-use-directly" )
@Alternative
public class DefaultUserManagerConfig
    extends DefaultCouchDBConfiguration
    implements UserManagerConfiguration
{

    private String adminEmail;

    private String adminPassword;

    private String adminFirstName;

    private String adminLastName;

    private CouchDBConfiguration dbConfig;

    public DefaultUserManagerConfig()
    {
    }

    public DefaultUserManagerConfig( final String adminEmail, final String adminPassword, final String adminFirstName,
                                     final String adminLastName, final String dbUrl )
    {
        super( dbUrl );
        this.adminEmail = adminEmail;
        this.adminPassword = adminPassword;
        this.adminFirstName = adminFirstName;
        this.adminLastName = adminLastName;
    }

    public DefaultUserManagerConfig( final String adminEmail, final String adminPassword, final String adminFirstName,
                                     final String adminLastName, final CouchDBConfiguration srcConfig,
                                     final String dbName )
    {
        super( srcConfig, dbName );

        this.adminEmail = adminEmail;
        this.adminPassword = adminPassword;
        this.adminFirstName = adminFirstName;
        this.adminLastName = adminLastName;
    }

    @Override
    public User createInitialAdminUser( final PasswordManager passwordManager )
    {
        final User user = new User( User.ADMIN );
        user.setEmail( adminEmail );
        user.setFirstName( adminFirstName );
        user.setLastName( adminLastName );

        user.setPasswordDigest( passwordManager.digestPassword( adminPassword ) );

        return user;
    }

    public String getAdminEmail()
    {
        return adminEmail;
    }

    public String getAdminPassword()
    {
        return adminPassword;
    }

    public String getAdminFirstName()
    {
        return adminFirstName;
    }

    public String getAdminLastName()
    {
        return adminLastName;
    }

    @ConfigName( "admin.email" )
    public void setAdminEmail( final String adminEmail )
    {
        this.adminEmail = adminEmail;
    }

    @ConfigName( "admin.password" )
    public void setAdminPassword( final String adminPassword )
    {
        this.adminPassword = adminPassword;
    }

    @ConfigName( "admin.firstname" )
    public void setAdminFirstName( final String adminFirstName )
    {
        this.adminFirstName = adminFirstName;
    }

    @ConfigName( "admin.lastname" )
    public void setAdminLastName( final String adminLastName )
    {
        this.adminLastName = adminLastName;
    }

    @Override
    public CouchDBConfiguration getDatabaseConfig()
    {
        if ( dbConfig == null )
        {
            dbConfig = new DefaultCouchDBConfiguration( getDatabaseUrl(), getMaxConnections() );
        }

        return dbConfig;
    }

}
