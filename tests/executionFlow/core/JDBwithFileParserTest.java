package executionFlow.core;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import executionFlow.info.ClassConstructorInfo;
import executionFlow.info.ClassMethodInfo;
import executionFlow.info.CollectorInfo;
import executionFlow.runtime.SkipCollection;

@SkipCollection
public class JDBwithFileParserTest 
{
	@Test
	public void parseAndDebugTest() throws IOException
	{
		String filename = "test_else.java";
		File inputFile = new File(filename);
		File originalFile = new File(filename+".original"); 
		
		
		// Creates copy of input file so that processing does not permanently alter it
		Files.copy(
				inputFile.toPath(), 
			originalFile.toPath(), 
			StandardCopyOption.REPLACE_EXISTING,
			StandardCopyOption.COPY_ATTRIBUTES
		);
		
		// Parses file
		FileParser fp = new FileParser("test_else.java", null, "test_else_parsed");
		File out = new File(fp.parseFile());
		
		// Changes parsed file name to the same as received filename
		inputFile.delete();
		out.renameTo(inputFile);
		
		// Compiles parsed file
		FileCompiler.compile(filename, "bin");
		
		// JDB
		/*
		String classPath = new File("bin/"+filename+".class").getAbsolutePath();
		List<CollectorInfo> collectorInfo = new ArrayList<>();
		List<List<Integer>> testPath_loop = new ArrayList<>();
		
		ClassMethodInfo sumMethod = new ClassMethodInfo.ClassMethodInfoBuilder()
				.testMethodSignature("test_else()")
				.classPath(classPath)
				.methodName("ifElseMethod")
				.methodSignature("")
				.invocationLine(28)
				.build();
		
		var constTypes = new Class<?>[] {int.class};
		ClassConstructorInfo loopCons = new ClassConstructorInfo(constTypes, 2);
		
		collectorInfo.add(new CollectorInfo(sumMethod,loopCons));
		
		JDB md = new JDB(29, false);
		testPath_loop = md.getTestPaths(sumMethod);
		*/
		
		// Undo changes
		inputFile.delete();
		originalFile.renameTo(inputFile);
	}
}
