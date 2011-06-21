package org.commonjava.web.fd.mail;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.mail.Address;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.commonjava.web.fd.config.FileDepotConfiguration;

public class MailMessage
{

    private final MailTemplate template;

    private final Set<InternetAddress> to = new HashSet<InternetAddress>();

    private Set<InternetAddress> cc;

    private Set<InternetAddress> bcc;

    private final Map<String, Object> properties = new HashMap<String, Object>();

    public MailMessage( final MailTemplate template, final String... to )
        throws MailException
    {
        this.template = template;

        to( to );
    }

    public MailMessage property( final String key, final Object value )
    {
        properties.put( key, value );
        return this;
    }

    public Map<String, Object> getProperties()
    {
        return properties;
    }

    public MailMessage to( final String... addresses )
        throws MailException
    {
        return addAddresses( this.to, addresses );
    }

    public MailMessage cc( final String... addresses )
        throws MailException
    {
        if ( this.cc == null )
        {
            this.cc = new HashSet<InternetAddress>();
        }

        return addAddresses( this.cc, addresses );
    }

    public MailMessage bcc( final String... addresses )
        throws MailException
    {
        if ( this.bcc == null )
        {
            this.bcc = new HashSet<InternetAddress>();
        }

        return addAddresses( this.bcc, addresses );
    }

    public MailTemplate getTemplate()
    {
        return template;
    }

    public MimeMessage build( final Session session, final FileDepotConfiguration config )
        throws MailException
    {
        final MimeMessage message = new MimeMessage( session );

        try
        {
            message.setSubject( config.getMailSubject( template.type(), template.defaultSubject() ) );

            message.addRecipients( RecipientType.TO, to.toArray( new Address[] {} ) );

            if ( cc != null && !cc.isEmpty() )
            {
                message.addRecipients( RecipientType.CC, cc.toArray( new Address[] {} ) );
            }

            if ( bcc != null && !bcc.isEmpty() )
            {
                message.addRecipients( RecipientType.BCC, bcc.toArray( new Address[] {} ) );
            }

            final InternetAddress[] from = InternetAddress.parse( config.getMailFromAddress() );
            message.addFrom( from );
        }
        catch ( final MessagingException e )
        {
            throw new MailException( "Failed to configure message headers: %s", e, e.getMessage() );
        }

        return message;
    }

    private MailMessage addAddresses( final Set<InternetAddress> type, final String... addrs )
        throws MailException
    {
        for ( final String addr : addrs )
        {
            InternetAddress[] addresses;
            try
            {
                addresses = InternetAddress.parse( addr );
            }
            catch ( final AddressException e )
            {
                throw new MailException( "Failed to parse email addresses: '%s'. Reason: %s", e, addr, e.getMessage() );
            }

            for ( final InternetAddress iAddr : addresses )
            {
                type.add( iAddr );
            }
        }

        return this;
    }

    public Set<InternetAddress> getCc()
    {
        return cc;
    }

    public void setCc( final Set<InternetAddress> cc )
    {
        this.cc = cc;
    }

    public Set<InternetAddress> getBcc()
    {
        return bcc;
    }

    public void setBcc( final Set<InternetAddress> bcc )
    {
        this.bcc = bcc;
    }

    public Set<InternetAddress> getTo()
    {
        return to;
    }

}
