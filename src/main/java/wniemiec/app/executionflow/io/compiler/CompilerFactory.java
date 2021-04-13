package wniemiec.app.executionflow.io.compiler;

import wniemiec.app.executionflow.io.compiler.aspectj.AspectJCompilerBuilder;
import wniemiec.app.executionflow.io.compiler.aspectj.StandardAspectJCompiler;

/**
 * Responsible for generating compilers.
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @since		6.0.0
 */
public class CompilerFactory {
	
	//-------------------------------------------------------------------------
	//		Constructor
	//-------------------------------------------------------------------------
	private CompilerFactory() {
	}
	
	
	//-------------------------------------------------------------------------
	//		Factories
	//-------------------------------------------------------------------------
	public static AspectJCompilerBuilder createStandardAspectJCompiler() {
		return new StandardAspectJCompiler.Builder();
	}
}
