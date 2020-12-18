package executionFlow.io.compiler;

import executionFlow.io.compiler.aspectj.AspectJCompilerBuilder;
import executionFlow.io.compiler.aspectj.StandardAspectJCompiler;

public class CompilerFactory {
	
	public static AspectJCompilerBuilder createStandardAspectJCompiler() {
		return new StandardAspectJCompiler.Builder();
	}
}
