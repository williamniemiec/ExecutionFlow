package executionflow.io;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import executionflow.ExecutionFlow;
import executionflow.io.FileEncoding;
import executionflow.io.compiler.Compiler;
import executionflow.io.compiler.CompilerFactory;
import executionflow.lib.LibraryManager;
import executionflow.runtime.SkipCollection;

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
					Path.of("bin", "executionflow", "runtime")
			);
		}
		
		return ExecutionFlow.getAppRootPath().resolve(
				Path.of("executionflow", "runtime")
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
