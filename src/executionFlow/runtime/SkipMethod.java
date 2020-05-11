package executionFlow.runtime;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.SOURCE;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * When a method has this annotation, {@link RuntimeColletor} will ignore it.
 */
//@Retention(SOURCE)
@Retention(RetentionPolicy.RUNTIME)
@Target(METHOD)
public @interface SkipMethod 
{

}
