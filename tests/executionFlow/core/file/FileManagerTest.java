package executionFlow.core.file;

import java.io.File;

import org.junit.Test;

import executionFlow.core.file.FileManager;
import executionFlow.core.file.parser.factory.InvokerFileParserFactory;
import executionFlow.core.file.parser.factory.TestMethodFileParserFactory;
import executionFlow.runtime.SkipCollection;


/**
 * Tests related to the functioning of class {@link FileManager}.
 */
@SkipCollection
public class FileManagerTest 
{
	/**
	 * Tests FileManager using {@link examples.controlFlow.TestClass_ControlFlow}.
	 * 
	 * @throws Exception If FileManager cannot parse or compile files
	 */
	@Test
	public void method_TestClass_ControlFlow() throws Exception
	{
		FileManager fileManager = new FileManager(
			"examples/controlFlow/TestClass_ControlFlow.java",
			new File("bin/controlFlow/").getAbsolutePath(),
			"controlFlow",
			new InvokerFileParserFactory()
		);
		
		try {
			fileManager.parseFile().createClassBackupFile().compileFile();
		} finally {
			fileManager.revertParse().revertCompilation();
		}
	}
	
	/**
	 * Tests FileManager using {@link examples.controlFlow.ControlFlowTest}.
	 * 
	 * @throws Exception If FileManager cannot parse or compile files
	 */
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
			fileManager.revertParse().revertCompilation();
		}
	}
}
