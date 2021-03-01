package wniemiec.executionflow.io;

import java.io.IOException;
import java.nio.file.Path;

import wniemiec.executionflow.App;
import wniemiec.util.io.search.FileSearcher;

/**
 * Searches for source or compiled files of a class.
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 */
public class ClassPathSearcher {

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
		System.out.println("@@@@" + App.getCurrentProjectRoot());
		FileSearcher searcher = new FileSearcher(App.getCurrentProjectRoot());
		String filename = generateCompiledFilename(removeParameters(classSignature));
		
		return searcher.search(filename);
	}
	
	private static String generateCompiledFilename(String classSignature) {
		StringBuilder filename = new StringBuilder();

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
		FileSearcher searcher = new FileSearcher(App.getCurrentProjectRoot());
		String filename = generateSrcFilename(removeParameters(classSignature));
		
		return searcher.search(filename);
	}
	
	private static String removeParameters(String classSignature) {
		if (!classSignature.contains("("))
			return classSignature;
		
		return classSignature.substring(0, classSignature.indexOf("("));
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
