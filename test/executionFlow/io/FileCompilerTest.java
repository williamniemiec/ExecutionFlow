package executionFlow.io;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import executionFlow.ExecutionFlow;
import executionFlow.LibraryManager;
import executionFlow.io.compiler.Compiler;
import executionFlow.io.compiler.CompilerFactory;
import executionFlow.runtime.SkipCollection;

@SkipCollection
public class FileCompilerTest {
	@Test 
	public void testCompileSimpleTestPathClass() throws IOException	{
		Path target = ExecutionFlow.getAppRootPath().resolve(Path.of(
				"examples", "examples", "others", "SimpleTestPath.java"));
		Path outputDir = ExecutionFlow.getAppRootPath().resolve("bin");

		Compiler compiler = CompilerFactory.createStandardAspectJCompiler()
				.inpath(generateAspectsRootDirectory())
				.classpath(generateClasspath())
				.build();
		compiler.compile(target, outputDir, FileEncoding.UTF_8);
	}
	
	private Path generateAspectsRootDirectory() {
		if (ExecutionFlow.isDevelopment()) {
			return ExecutionFlow.getAppRootPath().resolve(
					Path.of("bin", "executionFlow", "runtime")
			);
		}
		
		return ExecutionFlow.getAppRootPath().resolve(
				Path.of("executionFlow", "runtime")
		);
	}

	private List<Path> generateClasspath() {
		List<Path> classPaths = new ArrayList<>();
		
		classPaths.addAll(LibraryManager.getJavaClassPath());
		classPaths.add(LibraryManager.getLibrary("JUNIT_4"));
		classPaths.add(LibraryManager.getLibrary("HAMCREST"));
		classPaths.add(LibraryManager.getLibrary("ASPECTJRT"));
		classPaths.add(LibraryManager.getLibrary("JUNIT_5_API"));
		classPaths.add(LibraryManager.getLibrary("JUNIT_5_PARAMS"));
		
		return classPaths;
	}
}
