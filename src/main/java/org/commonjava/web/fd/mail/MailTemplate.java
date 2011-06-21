package org.commonjava.web.fd.mail;

public enum MailTemplate
{
    NEW_USER( "new-user", "new-user", "Your New Account" );

    private final String type;

    private final String defaultTemplate;

    private final String defaultSubject;

    private MailTemplate( final String type, final String defaultTemplate, final String defaultSubject )
    {
        this.type = type;
        this.defaultTemplate = defaultTemplate;
        this.defaultSubject = defaultSubject;
    }

    public String type()
    {
        return type;
    }

    public String defaultTemplate()
    {
        return defaultTemplate;
    }

    public String defaultSubject()
    {
        return defaultSubject;
    }
}
