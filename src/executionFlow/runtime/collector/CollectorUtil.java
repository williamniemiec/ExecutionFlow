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


/**
 * Helper class used by collectors.
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		5.2.3
 * @since		5.2.3
 */
public class CollectorUtil 
{
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	/**
	 * Necessary for {@link #findClassPath} method;
	 */
	private static Path binPath;
	
	private static Path srcPath;
	
	
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	/**
	 * Extracts package name of a method signature.
	 * 
	 * @param	signature Signature of the method
	 * 
	 * @return	Package name
	 */
	public static String extractPackageName(String signature)
	{
		String response = "";
		Pattern p = Pattern.compile("([A-z0-9\\-_$]+\\.)+");
		Matcher m = p.matcher(signature);

		if (m.find()) {
			String[] tmp = m.group().split("\\.");
			StringBuilder sb = new StringBuilder();
			
			for (int i=0; i<tmp.length-1; i++) {
				sb.append(tmp[i]);
				sb.append(".");
			}
			
			if (sb.length() > 0) {
				// Removes last dot
				sb.deleteCharAt(sb.length()-1);		
			}
			
			response = sb.toString();
		}
		
		return response;
	}
	
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
	 * Extracts class name from a signature.
	 * 
	 * @param	signature Signature of a method or class
	 * 
	 * @return	Name of this class or method
	 */
	public static String extractMethodName(String signature) 
	{
		String methodName = "";
		
		Pattern p = Pattern.compile("\\.[A-z0-9-_$]+\\(");
		Matcher m = p.matcher(signature);
		
		if (m.find()) {
			methodName = m.group();					// ".<methodName>("
			p = Pattern.compile("[A-z0-9-_$]+");
			m = p.matcher(methodName);
			
			if (m.find())
				methodName = m.group();				// "<methodName>"
		}
		
		return methodName;
	}
	
	
	
	/**
	 * Extracts the return type of a method.
	 * 
	 * @param	s Method signature
	 * 
	 * @return	Return type of the method
	 */
	public static String extractReturnType(Signature s)
	{
		String signature = s.toLongString();
		String[] terms = signature.substring(0, signature.indexOf("(")).split(" ");
		
		
		return terms[terms.length-2];
	}
	
	/**
	 * Gets directory where a test method is.
	 * 
	 * @param		testClassPath Test method file path
	 * 
	 * @return		Directory where a test method is
	 */
	public static String getTestClassDirectory(String testClassPath)
	{
		StringBuilder response = new StringBuilder();
		String[] terms = testClassPath.split("\\\\");
		
		
		for (int i=0; i<terms.length-1; i++) {
			response.append(terms[i]);
			response.append("\\");
		}
		
		if (response.length() > 0) {
			response.deleteCharAt(response.length()-1);
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

	public static String removeParametersFromSignature(String signature) 
	{
		return signature.substring(signature.indexOf("("));
	}
}
