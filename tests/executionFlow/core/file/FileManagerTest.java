package executionFlow.core.file;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import controlFlow.ControlFlowTest;
import controlFlow.TestClass_ControlFlow;
import executionFlow.core.file.FileManager;
import executionFlow.core.file.parser.factory.MethodFileParserFactory;
import executionFlow.core.file.parser.factory.TestMethodFileParserFactory;
import executionFlow.runtime.SkipCollection;


/**
 * Tests related to the functioning of class {@link FileManager}.
 * 
 * @apiNote All classes used by the tests are from class 
 * {@link TestClass_ControlFlow} and {@link ControlFlowTest}.
 */
@SkipCollection
public class FileManagerTest 
{
	@Test
	public void method_tryCatchTest1() throws Exception
	{
		FileManager fileManager = new FileManager(
			"examples/controlFlow/TestClass_ControlFlow.java",
			new File("bin/controlFlow/").getAbsolutePath(),
			"controlFlow",
			new MethodFileParserFactory()
		);
		
		try {
			fileManager.parseFile().compileFile();
		} finally {
			fileManager.revertParse();
		}
	}
	
	@Test
	public void testMethod_ControlFlowTest() throws Exception
	{
		FileManager fileManager = new FileManager(
			"examples/controlFlow/ControlFlowTest.java",
			new File("bin/controlFlow/").getAbsolutePath(),
			"controlFlow",
			new TestMethodFileParserFactory()
		);
		
		try {
			fileManager.parseFile()
				.parseFile()	
				.createClassBackupFile()
				.compileFile();
		} finally {
			fileManager.revertCompilation();
		}
	}
	/*
	@Test
	public void test_switch() throws Exception
	{
		FileManager fileManager = new FileManager(
			"tests/executionFlow/core/files/test_switch.java",
			"bin/executionFlow/core/files",
			"executionFlow.core.files",
			new MethodFileParserFactory()
		);
		
		String classPath = fileManager.parseFile().compileFile();
		//fileManager.revert();
		
		assertEquals("bin\\test_switch.class", classPath);
	}
	
	@Test
	public void testMethodFileParserTest() throws IOException
	{
		FileManager fileManager = new FileManager(
			"tests/executionFlow/core/tests/JUnitTest.java",
			"bin/executionFlow/core/tests",
			"executionFlow.core.files.tests",
			new TestMethodFileParserFactory()
		);
		
		fileManager.createClassBackupFile().parseFile().revertCompilation();
	}*/
}
