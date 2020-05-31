package executionFlow.runtime;

import static java.lang.annotation.ElementType.METHOD;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * When a method has this annotation, all collectors will ignore it.
 *  
 * @author William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @since 1.4
 * @version 1.4
 * 
 * @apiNote Must be used exclusively by 
 * {@link executionFlow.core.file.parser.FileParser}
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(METHOD)
public @interface _SkipMethod 
{}
