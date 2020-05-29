package executionFlow.runtime;

import static java.lang.annotation.ElementType.METHOD;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * When a method has this annotation, all collectors will ignore it.
 * 
 * @author William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @since 1.3
 * @version 1.3
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(METHOD)
public @interface SkipMethod 
{}
