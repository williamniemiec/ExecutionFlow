package executionFlow.core.file;

import java.io.File;

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
				.createClassBackupFile()
				.compileFile();
		} finally {
			fileManager.revertCompilation();
		}
	}
}
