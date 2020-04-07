package executionFlow.runtime;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import executionFlow.ExecutionFlow;


/**
 * Helper class used to create a bridge between data collected with AOP and {@link ExecutionFlow}
 */
public class CollectorExecutionFlow 
{
	//-----------------------------------------------------------------------
	//		Attributes
	//-----------------------------------------------------------------------
	private static String classPath;

	
	//-----------------------------------------------------------------------
	//		Methods
	//-----------------------------------------------------------------------
	/**
	 * Extracts signature of the test method.
	 * 
	 * @param signature First signature collected by aspect
	 * @return Signature of the test method
	 */
	public static String extractTestClassSignature(String signature)
	{
		String response = "";
		Pattern p = Pattern.compile("[A-z0-9-_$]+\\.\\<");
		Matcher m = p.matcher(signature);
		
		if (m.find()) {
			String name = m.group();
			p = Pattern.compile("[A-z0-9-_$]+");
			m = p.matcher(name);
			
			if (m.find()) {
				response = m.group();
			}
		}
		
		return response;
	}
	
	
	/**
	 * Extracts package name of a method signature.
	 * 
	 * @param signature Signature of the method
	 * @return Package name
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
			
			sb.deleteCharAt(sb.length()-1);		// Removes last dot
			response = sb.toString();
		}
		
		return response;
	}
	
	/**
	 * Given the parameters of a method, discover the class of each of these parameters.
	 * 
	 * @param args Parameter values of a method
	 * @return The classes of these parameter values
	 */
	public static Class<?>[] extractParamTypes(Object[] args) 
	{
		if (args == null || args.length == 0) { return null; }
		
		int i = 0;
		Class<?>[] paramTypes = new Class<?>[args.length];
		
		for (Object o : args) {
			paramTypes[i++] = normalizeClass(o.getClass());
		}
		
		return paramTypes;
	}
	
	/**
	 * Extracts class name from a signature.
	 * 
	 * @param signature Signature of a method or class
	 * @return Name of this class or method
	 */
	public static String extractClassName(String signature) 
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
	 * When executed it will determine the current location of the class executed.
	 * 
	 * @implSpec This method was projected to be executed in an AOP file. If
	 * it will execute in another place the results may be unexpected
	 * @return Absolute path of current execution class
	 * @throws IOException If class does not exist
	 */
	public static String findCurrentClassPath() throws IOException 
	{
		Path rootPath = Paths.get(System.getProperty("user.dir"));
		String aux = Thread.currentThread().getStackTrace()[3].getFileName();
		
		// <className>.java => <className>
		Pattern p = Pattern.compile("[A-z0-9-_$]+\\.");
		Matcher m = p.matcher(aux);
		
		if (m.find()) {				// <className>.java
			p = Pattern.compile("[A-z0-9-_$]+");
			m = p.matcher(m.group());
			
			if (m.find())
				aux = m.group();	// <className>
		}
		
		final String className = aux;
		
		Files.walkFileTree(rootPath, new SimpleFileVisitor<Path>() {
			@Override
		    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
		        if (file.toString().endsWith(className+".class")) {
		        	classPath = file.toString();
		        }
		        
		        return FileVisitResult.CONTINUE;
		    }
		});
		
		return classPath;
	}
	
	/**
	 * Converts a wrapper class in primitive. If the class is not a
	 * wrapper class, returns itself.
	 * 
	 * @param c Class to be normalized
	 * @return Normalized class
	 */
	private static Class<?> normalizeClass(Class<?> c)
	{
		Class<?> response = c;
		
		if 		(c == Boolean.class) 	{ response = boolean.class; }
		else if	(c == Byte.class) 		{ response = byte.class; }
		else if	(c == Character.class) 	{ response = char.class; }
		else if	(c == Short.class) 		{ response = short.class; }
		else if	(c == Integer.class)	{ response = int.class; }
		else if	(c == Float.class) 		{ response = float.class; }
		else if	(c == Long.class) 		{ response = long.class; }
		else if	(c == Double.class) 	{ response = double.class; }
		
		return response;
	}
}
