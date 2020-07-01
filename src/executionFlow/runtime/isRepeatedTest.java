package executionFlow.runtime;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Internal use annotation. Should be be added by 
 * {@link executionFlow.core.file.parser.PreTestMethodFileParser} when
 * processing a repeated test method.
 * 
 * @apiNote		Must be used exclusively by 
 * {@link executionFlow.core.file.parser.FileParser}
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		1.5
 * @since		1.5
 */
@Retention(RetentionPolicy.RUNTIME)
@Target( {ElementType.METHOD, ElementType.CONSTRUCTOR} )
public @interface isRepeatedTest 
{ }
