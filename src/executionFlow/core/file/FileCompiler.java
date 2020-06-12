package executionFlow.core.file;

import java.io.IOException;
import java.nio.file.Path;

import org.aspectj.bridge.IMessage;
import org.aspectj.bridge.MessageHandler;
import org.aspectj.tools.ajc.Main;

import executionFlow.ConsoleOutput;
import executionFlow.ExecutionFlow;


/**
 * Responsible for compiling .java files.
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		1.5
 * @since		1.3
 */
public class FileCompiler 
{
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	/**
	 * If true, displays shell output. 
	 */
	private static final boolean DEBUG;
	
	
	//-------------------------------------------------------------------------
	//		Initialization block
	//-------------------------------------------------------------------------
	/**
	 * Enables or disables debug. If activated, displays shell output during 
	 * compilation (performance can get worse).
	 */
	static {
		DEBUG = false;
	}
	
	
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	/**
	 * Compiles .java file.
	 * 
	 * @param		fileToCompile Path of source file to be compiled
	 * @param		outputDir Path where generated .class will be saved
	 * @param		encode File encoding
	 * @throws		IOException If an error occurs during compilation
	 */
	public static void compile(Path fileToCompile, Path outputDir, FileEncoding encode) throws IOException
	{
		Main compiler = new Main();
		MessageHandler m = new MessageHandler();
		String appRootPath = ExecutionFlow.getAppRootPath();
		String aspectsRootDirectory = appRootPath+"\\bin\\executionFlow\\runtime";
		
		compiler.run(
			new String[] {
				"-Xlint:ignore", 
				"-inpath", aspectsRootDirectory,
				"-9.0",
				"-encoding", 
				encode.getName(),
				"-classpath", outputDir.toAbsolutePath().toString()+";"
						+ appRootPath + "\\lib\\aspectjrt-1.9.2.jar" + ";"
						+ appRootPath + "\\lib\\junit-4.13.jar" + ";"
						+ appRootPath + "\\lib\\hamcrest-all-1.3.jar" + ";"
						+ appRootPath + "\\lib\\junit5\\junit-jupiter-api-5.6.2.jar",
				"-d", 
				outputDir.toAbsolutePath().toString(), 
				fileToCompile.toAbsolutePath().toString()
			},m);
		
		// -----{ DEBUG }-----
		if (DEBUG) {
			IMessage[] ms = m.getMessages(null, true);
			
			ConsoleOutput.showDebug("FileCompilator - start");
			for (var msg : ms) {
				ConsoleOutput.showDebug(msg.toString());
			}
			ConsoleOutput.showDebug("FileCompilator - end");
		}
		// -----{ END DEBUG }----
	}
}
