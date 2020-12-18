package executionFlow.io.compiler;

import java.nio.file.Path;
import java.util.List;

public interface AspectJCompilerBuilder {
	public AspectJCompilerBuilder inpath (Path inpath);
	public AspectJCompilerBuilder classpath(List<Path> classpath);
	public Compiler build();
}
