package executionFlow.io.compiler;

import executionFlow.io.compiler.aspectj.AspectJCompilerBuilder;
import executionFlow.io.compiler.aspectj.StandardAspectJCompiler;

/**
 * Responsible for generating compilers.
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		5.2.3
 * @since		5.2.3
 */
public class CompilerFactory {
	
	public static AspectJCompilerBuilder createStandardAspectJCompiler() {
		return new StandardAspectJCompiler.Builder();
	}
}
