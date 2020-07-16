package executionFlow.runtime;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Internal use annotation. When a method or a constructor has this annotation,
 * all collectors will ignore it.
 *  
 * @apiNote		Must be used exclusively by 
 * {@link executionFlow.io.processor.FileParser}
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		1.5
 * @since		1.5
 */
@Retention(RetentionPolicy.RUNTIME)
@Target( {ElementType.METHOD, ElementType.CONSTRUCTOR} )
public @interface _SkipInvoker 
{ }
