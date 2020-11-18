package executionFlow.io;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.aspectj.bridge.IMessage;
import org.aspectj.bridge.MessageHandler;
import org.aspectj.tools.ajc.Main;

import executionFlow.ExecutionFlow;
import executionFlow.LibraryManager;
import executionFlow.util.Logger;


/**
 * Responsible for compiling Java files.
 * 
 * @apiNote		Compatible with AspectJ
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		5.2.1
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
		Path appRootPath = ExecutionFlow.getAppRootPath();
		String aspectsRootDirectory = ExecutionFlow.isDevelopment() ? 
				appRootPath + "\\bin\\executionFlow\\runtime" : appRootPath + "\\executionFlow\\runtime";
		boolean error = false;
		
		
		compiler.run(
			new String[] {
				"-Xlint:ignore", 
				"-inpath", aspectsRootDirectory,
				"-9.0",
				"-encoding", 
				encode.getName(),
				"-classpath", 
						System.getProperty("java.class.path") + ";" +
						LibraryManager.getLibrary("JUNIT_4") + ";" +
						LibraryManager.getLibrary("HAMCREST") + ";" +
						LibraryManager.getLibrary("ASPECTJRT") + ";" +
						LibraryManager.getLibrary("JUNIT_5_API") + ";" +
						LibraryManager.getLibrary("JUNIT_5_PARAMS") + ";",
				"-d", 
				outputDir.toAbsolutePath().toString(), 
				target.toAbsolutePath().toString()
			},m);
		
		compiler.quit();
		
		IMessage[] ms = m.getMessages(null, true);
		
		Logger.debug("FileCompilator", "start");
		
		for (var msg : ms) {
			if (msg.toString().contains("error at"))
				error = true;
			
			Logger.debug(msg.toString());
		}
		
		Logger.debug("FileCompilator", "Output dir: " + outputDir.toAbsolutePath().toString());
		
		if (error)
			throw new IOException("Compilation error. Check the log for more information");
	}
}
