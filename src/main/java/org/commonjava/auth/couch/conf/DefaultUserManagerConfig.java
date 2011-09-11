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
package org.commonjava.auth.couch.conf;

import javax.enterprise.inject.Alternative;
import javax.inject.Named;

import org.commonjava.auth.couch.data.PasswordManager;
import org.commonjava.auth.couch.model.User;
import org.commonjava.util.logging.Logger;
import org.commonjava.web.config.annotation.ConfigName;
import org.commonjava.web.config.annotation.SectionName;

@SectionName( "user-manager" )
@Named( "unused" )
@Alternative
public class DefaultUserManagerConfig
    implements UserManagerConfiguration
{

    private final Logger logger = new Logger( getClass() );

    private String adminEmail;

    private String adminPassword;

    private String adminFirstName;

    private String adminLastName;

    private String databaseUrl;

    public DefaultUserManagerConfig()
    {}

    public DefaultUserManagerConfig( final String databaseUrl, final String adminEmail,
                                     final String adminPassword, final String adminFirstName,
                                     final String adminLastName )
    {
        this.databaseUrl = databaseUrl;
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

        logger.info( "Creating admin user; %s with password: %s", User.ADMIN, adminPassword );
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
    public String getDatabaseUrl()
    {
        return databaseUrl;
    }

    @ConfigName( "database.url" )
    public void setDatabaseUrl( final String databaseUrl )
    {
        this.databaseUrl = databaseUrl;
    }

}
