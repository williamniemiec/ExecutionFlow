package executionFlow.io;

import java.io.IOException;
import java.nio.file.Path;

import org.aspectj.bridge.IMessage;
import org.aspectj.bridge.MessageHandler;
import org.aspectj.tools.ajc.Main;

import executionFlow.ExecutionFlow;
import executionFlow.dependency.DependencyManager;
import executionFlow.util.ConsoleOutput;
import executionFlow.util.DataUtil;


/**
 * Responsible for compiling Java files.
 * 
 * @apiNote		Compatible with AspectJ
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		5.2.0
 * @since		1.3
 */
public class FileCompiler 
{		
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	/**
	 * Compiles .java file.
	 * 
	 * @param		target Path of source file to be compiled
	 * @param		outputDir Path where generated .class will be saved
	 * @param		encode File encoding
	 * 
	 * @throws		IOException If an error occurs during compilation
	 */
	public static void compile(Path target, Path outputDir, FileEncoding encode) throws IOException
	{
		Main compiler = new Main();
		MessageHandler m = new MessageHandler();
		String dependencies;
		String appRootPath = ExecutionFlow.getAppRootPath().toString();
		String aspectsRootDirectory = ExecutionFlow.isDevelopment() ? 
				appRootPath + "\\bin\\executionFlow\\runtime" : appRootPath + "\\executionFlow\\runtime";
		

		// Gets dependencies (if any)
		if (!DependencyManager.hasDependencies()) {
			DependencyManager.fetch();
		}
		
		dependencies = DataUtil.implode(DependencyManager.getDependencies(), ";");
		compiler.run(
			new String[] {
				"-Xlint:ignore", 
				"-inpath", aspectsRootDirectory,
				"-9.0",
				"-encoding", 
				encode.getName(),
				"-classpath", outputDir.toAbsolutePath().toString() + ";" +
						dependencies + ";" +
						appRootPath + "\\..\\classes;" +
						appRootPath + "\\..\\test-classes;" +
						appRootPath + "\\lib\\aspectjrt-1.9.2.jar;" +
						appRootPath + "\\lib\\junit-4.13.jar;" +
						appRootPath + "\\lib\\hamcrest-all-1.3.jar;" +
						appRootPath + "\\lib\\junit-jupiter-api-5.6.2.jar;" +
						appRootPath + "\\lib\\junit-jupiter-params-5.6.2.jar;",
				"-d", 
				outputDir.toAbsolutePath().toString(), 
				target.toAbsolutePath().toString()
			},m);
		
		compiler.quit();
		
		// -----{ DEBUG }-----
		if (ConsoleOutput.getLevel() == ConsoleOutput.Level.DEBUG) {
			IMessage[] ms = m.getMessages(null, true);
			
			ConsoleOutput.showDebug("FileCompilator", "start");
			
			for (var msg : ms) {
				ConsoleOutput.showDebug(msg.toString());
			}
			
			ConsoleOutput.showDebug("FileCompilator", "Output dir: " + outputDir.toAbsolutePath().toString());
		}
		// -----{ END DEBUG }----
	}
}
