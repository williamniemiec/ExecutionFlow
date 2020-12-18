package executionFlow.io.compiler;

public class CompilerFactory {
	
	public static AspectJCompilerBuilder createStandardAspectJCompiler() {
		return new StandardAspectJCompiler.Builder();
	}
}
