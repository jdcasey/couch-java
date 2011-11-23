package org.commonjava.auth.couch.inject;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.inject.Named;
import javax.inject.Qualifier;

import org.commonjava.couch.db.DataFlavor;

@Qualifier
@DataFlavor
@Named
@Retention( RetentionPolicy.RUNTIME )
@Target( { METHOD, FIELD, TYPE, PARAMETER } )
public @interface UserData
{

}
