package org.commonjava.auth.couch.inject;

import java.lang.annotation.ElementType;
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
@Target( {
    ElementType.FIELD,
    ElementType.METHOD,
    ElementType.TYPE } )
public @interface UserData
{

}
