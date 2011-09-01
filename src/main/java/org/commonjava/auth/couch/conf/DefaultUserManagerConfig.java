package org.commonjava.auth.couch.conf;

import javax.enterprise.inject.Alternative;
import javax.inject.Named;

import org.commonjava.auth.couch.data.PasswordManager;
import org.commonjava.auth.couch.data.UserViewRequest;
import org.commonjava.auth.couch.model.Permission;
import org.commonjava.auth.couch.model.Role;
import org.commonjava.auth.couch.model.User;
import org.commonjava.auth.couch.model.factory.DefaultPermissionCreator;
import org.commonjava.auth.couch.model.factory.DefaultRoleCreator;
import org.commonjava.auth.couch.model.factory.DefaultUserCreator;
import org.commonjava.auth.couch.model.factory.PermissionCreator;
import org.commonjava.auth.couch.model.factory.RoleCreator;
import org.commonjava.auth.couch.model.factory.UserCreator;
import org.commonjava.web.config.annotation.ConfigName;
import org.commonjava.web.config.annotation.SectionName;

@SectionName( "user-manager" )
@Named( "standalone" )
@Alternative
public class DefaultUserManagerConfig
    implements UserManagerConfiguration
{

    private String adminEmail;

    private String adminPassword;

    private String adminFirstName;

    private String adminLastName;

    private String databaseUrl;

    private String logicApplication = UserViewRequest.APPLICATION_RESOURCE;

    public DefaultUserManagerConfig()
    {}

    public DefaultUserManagerConfig( final String databaseUrl, final String logicApplication,
                                     final String adminEmail, final String adminPassword,
                                     final String adminFirstName, final String adminLastName )
    {
        this.databaseUrl = databaseUrl;
        this.logicApplication = logicApplication;
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
    public String getDatabaseUrl()
    {
        return databaseUrl;
    }

    @ConfigName( "database.url" )
    public void setDatabaseUrl( final String databaseUrl )
    {
        this.databaseUrl = databaseUrl;
    }

    @Override
    public String getLogicApplication()
    {
        return logicApplication;
    }

    @ConfigName( "db.application" )
    public void setLogicApplication( final String logicApplication )
    {
        this.logicApplication = logicApplication;
    }

    @Override
    public UserCreator<?> getUserCreator()
    {
        return new DefaultUserCreator();
    }

    @Override
    public RoleCreator<?> getRoleCreator()
    {
        return new DefaultRoleCreator();
    }

    @Override
    public PermissionCreator<?> getPermissionCreator()
    {
        return new DefaultPermissionCreator();
    }

    @SuppressWarnings( "unchecked" )
    @Override
    public <U extends User> Class<U> getUserClass()
    {
        return (Class<U>) User.class;
    }

    @SuppressWarnings( "unchecked" )
    @Override
    public <R extends Role> Class<R> getRoleClass()
    {
        return (Class<R>) Role.class;
    }

    @SuppressWarnings( "unchecked" )
    @Override
    public <P extends Permission> Class<P> getPermissionClass()
    {
        return (Class<P>) Permission.class;
    }
}
