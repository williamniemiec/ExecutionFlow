package executionFlow.runtime;

import static java.lang.annotation.ElementType.METHOD;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * When a method has this annotation, all invoked method signatures within it
 * will be collected.
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		1.5
 * @since		1.5
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(METHOD)
public @interface CollectInvokedMethods 
{ }
