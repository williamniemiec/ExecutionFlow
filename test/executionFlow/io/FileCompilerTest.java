package executionFlow.io;

import java.io.IOException;
import java.nio.file.Path;

import org.junit.Test;

import executionFlow.ExecutionFlow;
import executionFlow.io.compiler.aspectj.StandardAspectJCompiler;
import executionFlow.runtime.SkipCollection;
import executionFlow.util.logger.Logger;


@SkipCollection
public class FileCompilerTest 
{
	@Test 
	public void testCompileSimpleTestPathClass() throws IOException
	{
		Path target = ExecutionFlow.getAppRootPath().resolve(Path.of("examples", "examples", "others", "SimpleTestPath.java"));
		Path outputDir = ExecutionFlow.getAppRootPath().resolve("bin");
		
//		Logger.setLevel(Logger.Level.DEBUG);
		
		StandardAspectJCompiler.compile(target, outputDir, FileEncoding.UTF_8);
	}
}
