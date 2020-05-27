package executionFlow.core.file;

import java.nio.file.Path;

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
	 * @param fileToCompile Path of source file to be compiled
	 * @param outputDir Path where generated .class will be saved
	 * @param charset File encoding
	 * @return Path of generated .class
	 * @throws Exception If an error occurs during compilation
	 */
	public static String compile(Path fileToCompile, String outputDir, FileEncoding charset) throws Exception
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
				fileToCompile.toAbsolutePath().toString()
			}
		);
		
		if(compilationResult != 0) {
			throw new Exception("Compilation Failed");
		}
		
		String filename = fileToCompile.getName(fileToCompile.getNameCount()-1).toString();
		
		return outputDir+"\\"+filename.split("\\.")[0]+".class";
	}
}
