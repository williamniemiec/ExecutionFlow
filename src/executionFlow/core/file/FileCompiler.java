package executionFlow.core.file;

import java.io.File;

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
	 * @param charset File encoding
	 * @return Path of generated .class
	 * @throws Exception If an error occurs during compilation
	 */
	public static String compile(File fileToCompile, String outputDir, FileEncoding charset) throws Exception
	{
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		int compilationResult =	compiler.run(
			null, null, null, 
			new String[] {
				"-Xlint:none", 
				"-encoding", 
				charset.getText(),
				"-d", 
				outputDir, 
				fileToCompile.getAbsolutePath()
			}
		);
		
		if(compilationResult != 0) {
			throw new Exception("Compilation Failed");
		}
		
		return outputDir+"\\"+fileToCompile.getName().split("\\.")[0]+".class";
	}
}
