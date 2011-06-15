package org.commonjava.web.fd;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.shiro.crypto.hash.Sha256Hash;
import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class PasswordBuilder
{

    @Test
    public void generatePassword()
        throws IOException
    {
        final BufferedReader reader = new BufferedReader( new InputStreamReader( System.in ) );
        System.out.print( "Plaintext: " );
        final String line = reader.readLine();
        System.out.println( "Encrypted: " + new Sha256Hash( line ).toString() );
    }

}
