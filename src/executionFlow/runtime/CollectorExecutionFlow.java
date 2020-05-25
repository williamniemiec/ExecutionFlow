package executionFlow.runtime;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

import executionFlow.ExecutionFlow;


/**
 * Helper class used to extract the collected data that will be relevant to 
 * {@link ExecutionFlow} class.
 */
public class CollectorExecutionFlow 
{
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	/**
	 * Necessary for findCurrentClassPath method;
	 */
	private static String classPath;
	
	private static String srcPath;
	private static Path rootPath;
	
	
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
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
	
	public static String extractClassSignature(String methodSignature)
	{
		String[] tmp = methodSignature.split("\\.");
		StringBuilder response = new StringBuilder();
		
		for (int i=0; i<tmp.length-1; i++) {
			response.append(tmp[i]);
			response.append(".");
		}
		
		if (response.length() > 0)
			response.deleteCharAt(response.length()-1);		// Removes last dot
		
		return response.toString();
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
			paramTypes[i++] = o == null ? null : normalizeClass(o.getClass());
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
	 * When executed it will determine the absolute path of a class.
	 * 
	 * @param className Name of the class
	 * @param classSignature Signature of the class
	 * @return Absolute path of the class
	 * @throws IOException If class does not exist
	 */
	public static String findCurrentClassPath(String className, String classSignature) throws IOException 
	{
		if (rootPath == null) {
			rootPath = findProjectRoot().toPath();
		}
		
		// Gets folder where .class is
		String path = extractPathFromSignature(classSignature);
		
		// Finds absolute path where the class file is
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
	 * When executed it will determine the absolute path of a source file.
	 * 
	 * @param className Name of the class
	 * @param classSignature Signature of the class
	 * @return Absolute path of source file of current execution class
	 * @throws IOException If class does not exist
	 */
	public static String findCurrentSrcPath(String className, String classSignature) throws IOException 
	{
		if (rootPath == null) {
			rootPath = findProjectRoot().toPath();
		}
		
		// Gets path where .java is
		String path = extractPathFromSignature(classSignature);
//		System.out.println("PATH: "+path);
//		System.out.println(className);
//		System.out.println(classSignature);
//		System.out.println(path+className+".java");
//		System.out.println(rootPath);
		// Finds absolute path where the source file is
		Files.walkFileTree(rootPath, new SimpleFileVisitor<Path>() {
			@Override
		    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
		        if (file.toString().endsWith(path+className+".java")) {
		        	srcPath = file.toString();
//		        	System.out.println("FOUND! "+srcPath);
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
	 * @return Name of the package + name of the class + name of the method(param1, param2,...)
	 */
	public static String extractMethodSignature(String signature)
	{
		int index_endReturnType = signature.indexOf(' ');
		
		return signature.substring(index_endReturnType+1);
	}
	
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
	 * Extracts class name from a class signature.
	 * 
	 * @param classSignature Signature of the class
	 * @return Name of the class
	 */
	public static String getClassName(String classSignature)
	{
		String response;
		String[] tmp = classSignature.split("\\.");
		
		if (tmp.length < 1)
			response = tmp[0];
		else
			response = tmp[tmp.length-1];
		
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
	
	/**
	 * Finds project root. It will return the path that contains a directory 
	 * with name 'src'. 
	 * 
	 * @return Project root
	 */
	private static File findProjectRoot()
	{
		String[] allFiles;
		boolean hasSrcFolder = false;
		int i=0;
		File currentPath = new File(System.getProperty("user.dir"));
		
		// Searches for a path containing a directory named 'src'
		while (!hasSrcFolder) {
			allFiles = currentPath.list();
			
			// Checks the name of every file in current path
			i=0;
			while (!hasSrcFolder && i < allFiles.length) {
				// If there is a directory named 'src' stop the search
				if (allFiles[i].equals("src")) {
					hasSrcFolder = true;
				} else {
					i++;
				}
			}
			
			// If there is not a directory named 'src', it searches in the parent folder
			if (!hasSrcFolder) {
				currentPath = new File(currentPath.getParent());
			}
		}
		
		return currentPath;
	}
	
	/**
	 * Extracts file path from class signature. 
	 * 
	 * @param classSignature Signature of the class
	 * @return File path obtained from class signature
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
