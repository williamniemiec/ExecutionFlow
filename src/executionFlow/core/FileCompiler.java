package executionFlow.core;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;


/**
 * Responsible for compiling .java files.
 */
public class FileCompiler 
{
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	/**
	 * Compiles .java file.
	 * 
	 * @param fileToCompile Absolute path of source file to be compiled
	 * @param outputDir Path where generated .class will be saved
	 * @return Path of generated .class
	 * @throws Exception If an error occurs during compilation
	 */
	public static String compile(File fileToCompile, String outputDir) throws Exception
	{
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		int compilationResult =	compiler.run(null, null, null, new String[] {"-d", outputDir, fileToCompile.getAbsolutePath()});
		
		if(compilationResult != 0) {
			throw new Exception("Compilation Failed");
		}
		
		return outputDir+"\\"+fileToCompile.getName().split("\\.")[0]+".class";
	}
}
