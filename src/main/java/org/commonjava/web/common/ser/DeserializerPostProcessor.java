package org.commonjava.web.common.ser;

public interface DeserializerPostProcessor<T>
{

    void process( T value );

}
