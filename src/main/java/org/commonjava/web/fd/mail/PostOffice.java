package org.commonjava.web.fd.mail;

import java.io.StringWriter;

import javax.annotation.Resource;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;
import org.commonjava.web.fd.config.FileDepotConfiguration;

@ApplicationScoped
public class PostOffice
{

    @Resource( name = "mail/default_session" )
    private Session mailSession;

    @Inject
    private FileDepotConfiguration config;

    public void sendMessage( final MailMessage message )
        throws MailException
    {
        final VelocityEngine engine = new VelocityEngine( config.getTemplateEngineProperties() );

        final StringWriter writer = new StringWriter();
        final Context ctx = new VelocityContext( message.getProperties() );

        final MailTemplate template = message.getTemplate();

        final String templateName = config.getMailTemplate( template.type(), template.defaultTemplate() );
        engine.mergeTemplate( templateName, config.getTemplateEncoding(), ctx, writer );

        final MimeMessage msg = message.build( mailSession, config );

        try
        {
            final MimeBodyPart part = new MimeBodyPart();
            part.setText( writer.toString() );

            final Multipart multipart = new MimeMultipart();
            multipart.addBodyPart( part );

            msg.setContent( multipart );
        }
        catch ( final MessagingException e )
        {
            throw new MailException( "Failed to construct message body: %s", e, e.getMessage() );
        }

        try
        {
            Transport.send( msg );
        }
        catch ( final NoSuchProviderException e )
        {
            throw new MailException( "Failed to send message: %s", e, e.getMessage() );
        }
        catch ( final MessagingException e )
        {
            throw new MailException( "Failed to send message: %s", e, e.getMessage() );
        }
    }
}
