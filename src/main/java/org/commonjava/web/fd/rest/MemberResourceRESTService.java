package org.commonjava.web.fd.rest;

import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.commonjava.web.fd.data.MemberRepository;
import org.commonjava.web.fd.model.Member;

/**
 * JAX-RS Example This class produces a RESTful service to read the contents of the members table.
 */
@Path( "/members" )
@RequestScoped
public class MemberResourceRESTService
{
    @Inject
    @MemberRepository
    private EntityManager em;

    @GET
    public List<Member> listAllMembers()
    {
        // Use @SupressWarnings to force IDE to ignore warnings about "genericizing" the results of this query
        @SuppressWarnings( "unchecked" )
        // We recommend centralizing inline queries such as this one into @NamedQuery annotations on the @Entity class
        // as described in the named query blueprint:
        // https://blueprints.dev.java.net/bpcatalog/ee5/persistence/namedquery.html
        final List<Member> results = em.createQuery( "select m from Member m order by m.lastName" ).getResultList();
        return results;
    }

    @GET
    @Path( "/{id:[1-9][0-9]*}" )
    public Member lookupMemberById( @PathParam( "id" ) final long id )
    {
        return em.find( Member.class, id );
    }
}
