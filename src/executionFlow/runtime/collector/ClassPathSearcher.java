package executionFlow.runtime.collector;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.aspectj.lang.Signature;

import executionFlow.ExecutionFlow;


public class ClassPathSearcher 
{
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	private static Path binPath;
	private static Path srcPath;
	
	
	//-------------------------------------------------------------------------
	//		Constructor
	//-------------------------------------------------------------------------
	private ClassPathSearcher() {
	}
	
	
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	/**
	 * When executed it will determine the absolute path of a class.
	 * 
	 * @param		className Name of the class
	 * @param		classSignature Signature of the class
	 * 
	 * @return		Compiled file path or null if it cannot find the file
	 * 
	 * @throws		IOException If an error occurs while searching for the file
	 */
	public static Path findBinPath(String classSignature) throws IOException 
	{
		String className = extractClassNameFromClassSignature(classSignature);
		
		// Gets folder where .class is
		String path = extractPathFromSignature(classSignature);
		String prefix = ExecutionFlow.isDevelopment() ? "bin\\" : "";

		
		binPath = null;
		
		// Finds absolute path where the class file is
		Files.walkFileTree(ExecutionFlow.getCurrentProjectRoot(), new SimpleFileVisitor<Path>() {
			@Override
		    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
		        if (file.toString().endsWith(prefix+path+className+".class")) {
		        	file = Path.of(file.toAbsolutePath().toString().replaceAll("(\\/|\\\\)org(\\/|\\\\)org(\\/|\\\\)", "/org/"));
		        	binPath = file;
		        }
		        
		        return FileVisitResult.CONTINUE;
		    }
		});
		
		return binPath;
	}
	
	/**
	 * When executed it will determine the absolute path of a source file.
	 * 
	 * @param		className Name of the class
	 * @param		classSignature Signature of the class
	 * 
	 * @return		Compiled file path or null if it cannot find the file
	 * 
	 * @throws		IOException If an error occurs while searching for the file
	 */
	public static Path findSrcPath(String classSignature) throws IOException 
	{
		String className = extractClassNameFromClassSignature(classSignature);
		
		// Extracts parent class name from inner class (if it is one)
		final String effectiveClassName = className.split("\\$")[0];
		
		// Gets path where .java is
		String path = extractPathFromSignature(classSignature);
		
		
		srcPath = null;
		
		// Finds absolute path where the source file is
		Files.walkFileTree(ExecutionFlow.getCurrentProjectRoot(), new SimpleFileVisitor<Path>() {
			@Override
		    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
		        if (file.toString().endsWith(path+effectiveClassName+".java")) {
		        	file = Path.of(file.toAbsolutePath().toString().replaceAll("(\\/|\\\\)org(\\/|\\\\)org(\\/|\\\\)", "/org/"));
		        	srcPath = file;
		        	return FileVisitResult.TERMINATE;
		        }
		        
		        return FileVisitResult.CONTINUE;
		    }
		});
		
		return srcPath;
	}
	
	/**
	 * Gets class name from a class signature.
	 * 
	 * @param		classSignature Class signature
	 * 
	 * @return		Class name
	 */
	public static String extractClassNameFromClassSignature(String classSignature) 
	{
		String terms[] = classSignature.split("\\.");
		
		return terms[terms.length-1];
	}
	
	/**
	 * Extracts file path from class signature. <br />
	 * Example <br />
	 * <li>Class signature: <code>a.b.c()</code></li> 
	 * <li>File path: <code>a/b</code></li>
	 * 
	 * @param	classSignature Signature of the class
	 * 
	 * @return	File path obtained from class signature
	 */
	private static String extractPathFromSignature(String classSignature)
	{
		String terms[] = classSignature.split("\\.");
		
		if (terms.length == 1) { return "\\"; }
		
		StringBuilder response = new StringBuilder();
		
		// Finds path where the file is from class signature
		for (int i=0; i<terms.length-1; i++) {
			response.append(terms[i]);
			response.append("\\");
		}
		
		return response.toString();
	}
}
