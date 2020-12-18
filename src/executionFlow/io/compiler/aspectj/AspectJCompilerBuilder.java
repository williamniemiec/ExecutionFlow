package executionFlow.io.compiler.aspectj;

import java.nio.file.Path;
import java.util.List;

import executionFlow.io.compiler.Compiler;

public interface AspectJCompilerBuilder {
	public AspectJCompilerBuilder inpath (Path inpath);
	public AspectJCompilerBuilder classpath(List<Path> classpath);
	public Compiler build();
}
