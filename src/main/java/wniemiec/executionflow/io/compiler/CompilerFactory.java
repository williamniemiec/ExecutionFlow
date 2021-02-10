package wniemiec.executionflow.io.compiler;

import wniemiec.executionflow.io.compiler.aspectj.AspectJCompilerBuilder;
import wniemiec.executionflow.io.compiler.aspectj.StandardAspectJCompiler;

/**
 * Responsible for generating compilers.
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		6.0.0
 * @since		6.0.0
 */
public class CompilerFactory {
	
	public static AspectJCompilerBuilder createStandardAspectJCompiler() {
		return new StandardAspectJCompiler.Builder();
	}
}
