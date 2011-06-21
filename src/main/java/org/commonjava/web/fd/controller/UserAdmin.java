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
import org.commonjava.web.fd.data.UserDataManager.UserRepository;
import org.commonjava.web.fd.mail.MailException;
import org.commonjava.web.fd.mail.PostOffice;
import org.commonjava.web.fd.mail.MailMessage;
import org.commonjava.web.fd.mail.MailTemplate;
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

    @Inject
    private PostOffice mailManager;

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
        HeuristicRollbackException, MailException
    {
        logger.info( "\n\nSaving user: %s\n\n", newUser );

        logger.info( "Generating password." );
        final String password = passwordManager.generatePassword();

        final MailMessage message = new MailMessage( MailTemplate.NEW_USER, newUser.getEmail() );
        message.property( "user", newUser );
        message.property( "password", password );

        mailManager.sendMessage( message );

        logger.info( "Encrypting password." );
        newUser.setPassword( passwordManager.digestPassword( password ) );

        tx.begin();
        em.joinTransaction();
        em.persist( newUser );
        tx.commit();
        eventSrc.fire( newUser );
        createNewUserInstance();
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
    private void createNewUserInstance()
    {
        newUser = new User();
    }

}
