package wniemiec.app.executionflow.runtime;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * When a method or a constructor has this annotation, the signature of all
 * methods called within it will be collected.
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @since		2.0.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.CONSTRUCTOR})
public @interface CollectCalls {
}
