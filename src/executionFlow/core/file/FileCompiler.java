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
	//		Methods
	//-------------------------------------------------------------------------
	/**
	 * Compiles .java file.
	 * 
	 * @param		fileToCompile Path of source file to be compiled
	 * @param		outputDir Path where generated .class will be saved
	 * @param		encode File encoding
	 * @param		debug If true, displays compiler output
	 * @throws		IOException If an error occurs during compilation
	 */
	public static void compile(Path fileToCompile, Path outputDir, FileEncoding encode, boolean debug) throws IOException
	{
		Main compiler = new Main();
		MessageHandler m = new MessageHandler();
		
		String aspectsRootDirectory = ExecutionFlow.getAppRootPath()+"\\bin\\executionFlow\\runtime";
		
		compiler.run(
			new String[] {
				"-Xlint:ignore", 
				"-inpath", aspectsRootDirectory,
				"-source","1.9",
				"-encoding", 
				encode.getName(),
				"-d", 
				outputDir.toAbsolutePath().toString(), 
				fileToCompile.toAbsolutePath().toString()
			},m);
		
		// -----{ DEBUG }-----
		if (debug) {
			IMessage[] ms = m.getMessages(null, true);
			
			ConsoleOutput.showDebug("FileCompilator - start");
			for (var msg : ms) {
				ConsoleOutput.showDebug(msg.toString());
			}
			ConsoleOutput.showDebug("FileCompilator - end");
		}
		// -----{ END DEBUG }----
	}
	
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
		compile(fileToCompile, outputDir, encode, false);
	}
}
