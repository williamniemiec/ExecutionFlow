package executionFlow.runtime;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Internal use annotation. Should be be added by 
 * {@link executionFlow.io.processor.PreTestMethodFileProcessor} when
 * processing a repeated test method.
 * 
 * @apiNote		Must be used exclusively by 
 * {@link executionFlow.io.processor.FileProcessor}
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		2.0.0
 * @since		2.0.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target( {ElementType.METHOD, ElementType.CONSTRUCTOR} )
public @interface isRepeatedTest 
{ }
