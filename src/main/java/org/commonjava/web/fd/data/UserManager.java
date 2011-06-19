package org.commonjava.web.fd.data;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.event.Reception;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Qualifier;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.commonjava.web.fd.model.User;

@RequestScoped
public class UserManager
{
    @Inject
    @UserRepository
    private EntityManager em;

    private List<User> users;

    @Produces
    @Named
    public List<User> getUsers()
    {
        return users;
    }

    public void onUserChanged( @Observes( notifyObserver = Reception.IF_EXISTS ) final User user )
    {
        loadAllUsersOrderedByLastNameThenFirstName();
    }

    @PostConstruct
    public void loadAllUsersOrderedByLastNameThenFirstName()
    {
        final CriteriaBuilder cb = em.getCriteriaBuilder();
        final CriteriaQuery<User> query = cb.createQuery( User.class );
        final Root<User> root = query.from( User.class );

        query.select( root )
             .orderBy( cb.asc( root.get( "lastName" ) ), cb.asc( root.get( "firstName" ) ) );

        users = em.createQuery( query )
                  .getResultList();
    }

    public static final class UserRepositoryProducer
    {
        @SuppressWarnings( "unused" )
        @Produces
        @UserRepository
        @PersistenceContext
        private EntityManager em;
    }

    @Qualifier
    @Target( { ElementType.TYPE, ElementType.METHOD, ElementType.PARAMETER, ElementType.FIELD } )
    @Retention( RetentionPolicy.RUNTIME )
    public @interface UserRepository
    {
    }

}
