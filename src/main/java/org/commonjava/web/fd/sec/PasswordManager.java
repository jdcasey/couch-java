package org.commonjava.web.fd.sec;

import java.security.SecureRandom;

import javax.enterprise.context.ApplicationScoped;

import org.apache.shiro.crypto.hash.Sha512Hash;

@ApplicationScoped
public class PasswordManager
{

    private static final String ONETIME_PASSWORD_SEED = "23456789abcdefghkmnpqrstuvwxyzABCDEFGHKMNPQRSTUVWXYZ-_!.";

    private static final int ONETIME_PASSWORD_LENGTH = 15;

    private final SecureRandom randomGenerator = new SecureRandom();

    public String generateOneTimePassword()
    {
        final StringBuilder sb = new StringBuilder();
        for ( int i = 0; i < ONETIME_PASSWORD_LENGTH; i++ )
        {
            final int idx = Math.abs( randomGenerator.nextInt() ) % ONETIME_PASSWORD_SEED.length();
            sb.append( ONETIME_PASSWORD_SEED.charAt( idx ) );
        }

        return sb.toString();
    }

    public boolean verifyPassword( final String digest, final String password )
    {
        return digest.equals( new Sha512Hash( password ).toHex() );
    }

    public String digestPassword( final String password )
    {
        return new Sha512Hash( password ).toHex();
    }

}
