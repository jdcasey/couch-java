package org.commonjava.web.test.fixture;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.enterprise.inject.Stereotype;
import javax.inject.Named;
import javax.inject.Qualifier;

@Stereotype
@Named
@Qualifier
@Retention( RetentionPolicy.RUNTIME )
@Target( {
    ElementType.FIELD,
    ElementType.METHOD,
    ElementType.TYPE } )
public @interface TestData
{

}
