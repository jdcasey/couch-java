package org.commonjava.web.fd.data;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.event.Reception;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.commonjava.web.fd.model.Member;

@RequestScoped
public class MemberListProducer
{
    @Inject
    @MemberRepository
    private EntityManager em;

    private List<Member> members;

    // @Named provides access the return value via the EL variable name "member" in the UI (e.g., Facelets or JSP view)
    @Produces
    @Named
    public List<Member> getMembers()
    {
        return members;
    }

    public void onMemberListChanged( @Observes( notifyObserver = Reception.IF_EXISTS ) final Member member )
    {
        retrieveAllMembersOrderedByName();
    }

    @PostConstruct
    public void retrieveAllMembersOrderedByName()
    {
        final CriteriaBuilder cb = em.getCriteriaBuilder();
        final CriteriaQuery<Member> criteria = cb.createQuery( Member.class );
        final Root<Member> member = criteria.from( Member.class );
        // Swap criteria statements if you would like to try out type-safe criteria queries, a new feature in JPA 2.0
        // criteria.select(member).orderBy(cb.asc(member.get(Member_.name)));
        criteria.select( member ).orderBy( cb.asc( member.get( "lastName" ) ) );
        members = em.createQuery( criteria ).getResultList();
    }
}
