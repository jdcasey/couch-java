package org.commonjava.auth.shiro.couch.conf;

import javax.enterprise.inject.Alternative;
import javax.inject.Named;

import org.commonjava.auth.couch.conf.DefaultUserManagerConfig;
import org.commonjava.auth.couch.model.Permission;
import org.commonjava.auth.couch.model.factory.PermissionCreator;
import org.commonjava.auth.shiro.couch.model.ShiroPermission;
import org.commonjava.auth.shiro.couch.model.factory.ShiroPermissionCreator;
import org.commonjava.web.config.annotation.SectionName;

@SectionName( "user-manager" )
@Named( "standalone" )
@Alternative
public class ShiroUserManagerConfig
    extends DefaultUserManagerConfig
{

    public ShiroUserManagerConfig()
    {}

    public ShiroUserManagerConfig( final String databaseUrl, final String logicApplication,
                                   final String adminEmail, final String adminPassword,
                                   final String adminFirstName, final String adminLastName )
    {
        super( databaseUrl, logicApplication, adminEmail, adminPassword, adminFirstName,
               adminLastName );
    }

    @Override
    public PermissionCreator<?> getPermissionCreator()
    {
        return new ShiroPermissionCreator();
    }

    @SuppressWarnings( "unchecked" )
    @Override
    public <P extends Permission> Class<P> getPermissionClass()
    {
        return (Class<P>) ShiroPermission.class;
    }
}
