package org.commonjava.web.fd.controller;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Event;
import javax.enterprise.inject.Model;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import org.commonjava.util.logging.Logger;
import org.commonjava.web.fd.data.UserManager.UserRepository;
import org.commonjava.web.fd.model.User;
import org.commonjava.web.fd.sec.PasswordManager;

@Model
public class UserAdmin
{

    private final Logger logger = new Logger( getClass() );

    @Inject
    @UserRepository
    private EntityManager em;

    @Inject
    private Event<User> eventSrc;

    @Inject
    private UserTransaction tx;

    private PasswordManager passwordManager;

    private User newUser;

    @Produces
    @Named
    public User getNewUser()
    {
        return newUser;
    }

    public void createUser()
        throws NotSupportedException, SystemException, RollbackException, HeuristicMixedException,
        HeuristicRollbackException
    {
        logger.info( "\n\nSaving user: %s\n\n", newUser );

        logger.info( "Encrypting password." );
        newUser.setPassword( passwordManager.digestPassword( newUser.getPassword() ) );

        tx.begin();
        em.joinTransaction();
        em.persist( newUser );
        tx.commit();
        eventSrc.fire( newUser );
        createUserInstance();
    }

    public void generateUsername()
    {
        if ( newUser.getLastName() != null && newUser.getFirstName() != null )
        {
            final StringBuilder sb = new StringBuilder();

            sb.append( Character.toLowerCase( newUser.getFirstName()
                                                     .charAt( 0 ) ) );
            sb.append( Character.toLowerCase( newUser.getLastName()
                                                     .charAt( 0 ) ) );

            if ( newUser.getLastName()
                        .length() > 1 )
            {
                sb.append( newUser.getLastName()
                                  .substring( 1 ) );
            }

            newUser.setUsername( sb.toString() );
        }
    }

    @PostConstruct
    private void createUserInstance()
    {
        newUser = new User();
    }

}
