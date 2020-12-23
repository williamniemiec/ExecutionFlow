package executionFlow.runtime.collector;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import executionFlow.ExecutionFlow;

/**
 * Searches for source or compiled files of a class.
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		5.2.3
 * @since		1.0
 */
public class ClassPathSearcher {
	
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
	public static Path findBinPath(String classSignature) throws IOException {
		String filename = generateCompiledFilename(classSignature);

		binPath = null;
		
		Files.walkFileTree(ExecutionFlow.getCurrentProjectRoot(), new SimpleFileVisitor<Path>() {
			@Override
		    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
		        if (file.toString().endsWith(filename)) {
		        	file = fixOrg(file);
		        	binPath = file;
		        }
		        
		        return FileVisitResult.CONTINUE;
			}
		});
		
		return binPath;
	}
	
	private static String generateCompiledFilename(String classSignature) {
		StringBuilder filename = new StringBuilder();

		filename.append(ExecutionFlow.isDevelopment() ? "bin\\" : "");
		filename.append(extractPathFromSignature(classSignature));
		filename.append(extractClassNameFromClassSignature(classSignature));
		filename.append(".class");
		
		return filename.toString();
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
	private static String extractPathFromSignature(String classSignature) {
		String terms[] = classSignature.split("\\.");
		
		if (terms.length == 1)
			return "\\";
		
		StringBuilder response = new StringBuilder();
		
		for (int i=0; i<terms.length-1; i++) {
			response.append(terms[i]);
			response.append("\\");
		}
		
		return response.toString();
	}
	
	/**
	 * Gets class name from a class signature.
	 * 
	 * @param		classSignature Class signature
	 * 
	 * @return		Class name
	 */
	private static String extractClassNameFromClassSignature(String classSignature) {
		String terms[] = classSignature.split("\\.");
		
		return terms[terms.length-1];
	}
	
	private static Path fixOrg(Path file) {
		return Path.of(file.toAbsolutePath().toString().replaceAll(
				"(\\/|\\\\)org(\\/|\\\\)org(\\/|\\\\)", 
				"/org/"
		));
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
	public static Path findSrcPath(String classSignature) throws IOException {
		String filename = generateSrcFilename(classSignature);	
		
		srcPath = null;
		
		Files.walkFileTree(ExecutionFlow.getCurrentProjectRoot(), new SimpleFileVisitor<Path>() {
			@Override
		    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
		        if (file.toString().endsWith(filename)) {
		        	file = fixOrg(file);
		        	srcPath = file;
		        	return FileVisitResult.TERMINATE;
		        }
		        
		        return FileVisitResult.CONTINUE;
		    }
		});
		
		return srcPath;
	}
	
	private static String generateSrcFilename(String classSignature) {
		StringBuilder filename = new StringBuilder();
		String className = extractClassNameFromClassSignature(classSignature);
		
		filename.append(extractPathFromSignature(classSignature));
		filename.append(extractEffectiveClassName(className));
		filename.append(".java");
		
		return filename.toString();
	}
	
	private static String extractEffectiveClassName(String className) {
		return className.split("\\$")[0];
	}
}
