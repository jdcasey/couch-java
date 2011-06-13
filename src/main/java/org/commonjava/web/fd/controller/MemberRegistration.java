package org.commonjava.web.fd.controller;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Event;
import javax.enterprise.inject.Model;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;

import org.commonjava.web.fd.data.MemberRepository;
import org.commonjava.web.fd.model.Member;

// Adding the @Stateful annotation eliminates need for manual transaction demarcation
// @javax.ejb.Stateful
// The @Model stereotype is a convenience mechanism to make this a request-scoped bean that has an EL name
// Read more about the @Model stereotype in this FAQ: http://sfwk.org/Documentation/WhatIsThePurposeOfTheModelAnnotation
@Model
public class MemberRegistration
{
    // private final Logger log = new Logger( getClass() );

    @Inject
    @MemberRepository
    private EntityManager em;

    // @Inject
    // private UserTransaction utx;

    @Inject
    private Event<Member> memberEventSrc;

    private Member newMember;

    @Produces
    @Named
    public Member getNewMember()
    {
        return newMember;
    }

    public void register()
    throws Exception
    {
        // log.info( "Registering " + newMember.getFirstName() + " " + newMember.getLastName() );
        // UserTransaction only needed when bean is not an EJB
        // utx.begin();
        em.joinTransaction();
        em.persist( newMember );
        // utx.commit();
        memberEventSrc.fire( newMember );
        initNewMember();
    }

    @PostConstruct
    public void initNewMember()
    {
        newMember = new Member();
    }
}
