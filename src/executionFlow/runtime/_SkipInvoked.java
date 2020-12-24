package executionflow.runtime;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Internal use annotation. When a method or a constructor has this annotation,
 * the following collectors will ignore it:
 * <ul>
 * 	<li>{@link executionflow.runtime.collector.ConstructorCollector}</li>
 * 	<li>{@link executionflow.runtime.collector.MethodCollector}</li>
 * 	<li>{@link executionflow.runtime.collector.TestMethodCollector}</li>
 * </ul>
 *  
 * @apiNote		Must be used exclusively by 
 * {@link executionflow.io.processor.FileProcessor}
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		2.0.0
 * @since		2.0.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.CONSTRUCTOR})
public @interface _SkipInvoked 
{}
