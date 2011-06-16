package org.commonjava.web.fd;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.shiro.crypto.hash.SimpleHash;

public class PasswordBuilder
{
    public static void main( final String[] args )
        throws IOException
    {
        final BufferedReader reader = new BufferedReader( new InputStreamReader( System.in ) );
        System.out.print( "Password: " );
        final String line = reader.readLine();
        final SimpleHash hash = new SimpleHash( "SHA-512", line, null, 1 );
        System.out.println( "Encrypted: " + hash.toBase64() );
    }

}
