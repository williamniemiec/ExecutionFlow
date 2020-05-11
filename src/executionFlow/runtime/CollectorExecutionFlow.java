package executionFlow.runtime;

import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

import executionFlow.ExecutionFlow;


/**
 * Helper class used to extract the collected data that will be relevant to {@link ExecutionFlow} class.
 */
public class CollectorExecutionFlow 
{
	//-----------------------------------------------------------------------
	//		Attributes
	//-----------------------------------------------------------------------
	/**
	 * Necessary for findCurrentClassPath method;
	 */
	private static String classPath;
	
	private static String srcPath;

	
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
			
			if (sb.length() > 0)
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
	 * Extracts parameter types of a method.
	 * 
	 * @param jp JoinPoint with the method
	 * @return Classes of parameter types of the method
	 */
	public static Class<?>[] extractParamTypes(JoinPoint jp)
	{
		Method method = ((MethodSignature) jp.getSignature()).getMethod();
		return method.getParameterTypes();
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
//		System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!");
		
		// Gets folder where .class is
		String terms[] = Thread.currentThread().getStackTrace()[3].getClassName().split("\\.");
		StringBuilder sb = new StringBuilder();
		
		for (int i=0; i<terms.length-1; i++) {
			sb.append(terms[i]);
			sb.append("\\");
		}
		
		String path = sb.toString();
		
//		System.out.println(Thread.currentThread().getStackTrace()[3].getClassName());
//		System.out.println(Thread.currentThread().getStackTrace()[3].getClass().getPackageName());
//		System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!");
		
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
		        if (file.toString().endsWith(path+className+".class")) {
		        	classPath = file.toString();
		        }
		        
		        return FileVisitResult.CONTINUE;
		    }
		});
		
		return classPath;
	}
	
	/**
	 * When executed it will determine the current location of the class executed.
	 * 
	 * @implSpec This method was projected to be executed in an AOP file. If
	 * it will execute in another place the results may be unexpected
	 * @return Absolute path of current execution class
	 * @throws IOException If class does not exist
	 */
	public static String findCurrentSrcPath() throws IOException 
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
		
		// Gets folder where .java is
		String terms[] = Thread.currentThread().getStackTrace()[3].getClassName().split("\\.");
		StringBuilder sb = new StringBuilder();
		
		for (int i=0; i<terms.length-1; i++) {
			sb.append(terms[i]);
			sb.append("\\");
		}
		
		String path = sb.toString();
		
		final String className = aux;
		
		Files.walkFileTree(rootPath, new SimpleFileVisitor<Path>() {
			@Override
		    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
		        if (file.toString().endsWith(path+className+".java")) {
		        	srcPath = file.toString();
		        	
		        	return FileVisitResult.TERMINATE;
		        }
		        
		        return FileVisitResult.CONTINUE;
		    }
		});
		
		return srcPath;
	}
	
	/**
	 * Extracts method's class signature.
	 * 
	 * @param signature Signature of the method
	 * @return Name of the package + name of the class
	 */
	/*
	public static String extractMethodSignature(String signature)
	{
		System.out.println("!!!!"+signature);
		signature = signature.split(" ")[1];		// Removes return
		String[] tmp = signature.split("\\.");
		StringBuilder response = new StringBuilder();
		
		for (int i=0; i<tmp.length-1; i++) {
			response.append(tmp[i]);
			response.append(".");
		}
		
		if (response.length() > 0) {
			response.deleteCharAt(response.length()-1);	// Removes last dot
		}
		
		return response.toString();
	}
	*/
	/**
	 * Extracts class name from a signature.
	 * 
	 * @param signature Signature of a method or class
	 * @return Name of this class or method
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
	 * Extracts class name from a method signature.
	 * 
	 * @param signature Method signature
	 * @return Class name
	 */
	public static String getClassName(String signature)
	{
		String response;
		String[] tmp = signature.split("\\.");
		
		if (tmp.length < 2)
			response = tmp[0];
		else
			response = tmp[tmp.length-2];
		
		return response;	
	}
	
	/**
	 * Extracts return type of a method.
	 * 
	 * @param jp JoinPoint with the method
	 * @return Class of return type of the method
	 */
	public static Class<?> extractReturnType(JoinPoint jp)
	{
		Method method = ((MethodSignature) jp.getSignature()).getMethod();
		return method.getReturnType();
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
