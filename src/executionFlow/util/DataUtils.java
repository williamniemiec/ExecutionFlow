package executionFlow.util;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * Contains methods that perform data manipulation.
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		2.0.0
 * @since		2.0.0
 */
public class DataUtils 
{
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	/**
	 * Converts elements of a list into a string by separating each element
	 * with a delimiter. 
	 * 
	 * @param		list List to be converted
	 * 
	 * @return		List elements separated by the given delimiter
	 */
	public static <T> String implode(List<T> list, String delimiter)
	{
		StringBuilder response = new StringBuilder();
		
		
		for (T p : list) {
			response.append(p);
			response.append(delimiter);
		}
		
		// Removes last semicolon
		if (response.length() > 1) {
			response.deleteCharAt(response.length()-1);
		}
		
		return response.toString();
	}
	
	/**
	 * Given two Maps, adds all content from the first Map to the second.
	 * 
	 * @param		map1 Some map
	 * @param		map2 Map that will be merge with map1 
	 */
	public static void mergesMaps(Map<String, List<String>> map1, Map<String, List<String>> map2)
	{
		for (Map.Entry<String, List<String>> e : map1.entrySet()) {
			String keyMap1 = e.getKey();
			List<String> contentMap2;

			
			// Adds content from first Map to the second
			for (String contentMap1 : e.getValue()) {
				// If second Map contains the same key as the first, add all
				// the content of this key from first Map in the second
				if (map2.containsKey(keyMap1)) {
					contentMap2 = map2.get(keyMap1);
					
					if (!contentMap2.contains(contentMap1)) {
						contentMap2.add(contentMap1);
					}
				}
				else {
					contentMap2 = new ArrayList<>();
					contentMap2.add(contentMap1);
					map2.put(keyMap1, contentMap2);
				}
			}
		}
	}
	
	/**
	 * Generates a path based on an invoked signature with the following format:
	 * <ul>
	 * 	<li><b>Invoked = method:</b> <code>package/className.methodName(parameterTypes)</code></li>
	 * 	<li><b>Invoked = constructor:</b> <code>package/className(parameterTypes)</code></li>
	 * </ul>
	 * 
	 * <h1>Example</h1>
	 * <ul>
	 * 	<li><b>Invoked signature (method):</b> controlFlow.TestClass_ControlFlow.ifElseMethod(int)</li>
	 * 	<li><b>Generated path:</b> controlFlow/TestClass_ControlFlow.ifElseMethod(int)</li>
	 * </ul>
	 * 
	 * @param		invokedSignature Invoked signature
	 * @param		isConstructor If the invoked is a constructor
	 * 
	 * @return		Generated path
	 */
	public static String generateDirectoryPath(String invokedSignature, boolean isConstructor)
	{
		String[] signatureFields = invokedSignature.split("\\.");
		String folderPath = getFolderPath(signatureFields, isConstructor);
		String folderName = getFolderName(signatureFields, isConstructor);
		
		
		return folderPath+"/"+folderName;
	}
	
	/**
	 * Generates folder's path based on invoked's signature. It will generates
	 * path following the package of this invoked.
	 * 
	 * <h1>Format</h1>
	 * <ul>
	 * 	<li><b>Invoked signature (method):</b> package1.package2.package3.className.methodName(parameter types)</li>
	 * 	<li><b>Folder path:</b> package1/packag2/package3</li>
	 * </ul>
	 * 
	 * @param		signatureFields Fields of the invoked signature
	 * @param		isConstructor If the invoked is a constructor
	 * 
	 * @return		Folder's path
	 */
	private static String getFolderPath(String[] signatureFields, boolean isConstructor)
	{
		String folderPath = "";
		StringBuilder sb = new StringBuilder();
		int size = isConstructor ? signatureFields.length-1 : signatureFields.length-2;
		
		
		for (int i=0; i<size; i++) {
			sb.append(signatureFields[i]);
			sb.append("/");
		}
		
		if (sb.length() > 0) {
			sb.deleteCharAt(sb.length()-1);	// Removes last slash
			folderPath = sb.toString();
		}
		
		return folderPath;
	}
	
	/**
	 * Generates folder's name based on invoked's signature. This name will be:
	 * <code>className.invokedName(parameter types)</code>
	 * 
	 * @param		signatureFields Fields of the invoked signature
	 * @param		If signatureFields referes to a constructor signature
	 * 
	 * @return		Folder's name
	 */
	private static String getFolderName(String[] signatureFields, boolean isConstructor)
	{
		String response = null;
		
		
		if (isConstructor) {
			// Extracts class name
			String className = signatureFields[signatureFields.length-1];
			
			if (!className.contains("("))
				className += "()";
			
			response = className;
		}
		else {
			String className, methodName;
			
			
			// Extracts class name
			className = signatureFields[signatureFields.length-2];
			
			// Extracts invoked name with parameter types
			methodName = signatureFields[signatureFields.length-1];
			
			response = className+"."+methodName;
		}
		
		return response;
	}
	
	/**
	 * Encrypts a text in MD5.
	 * 
	 * @param		text Text to be encrypted
	 * 
	 * @return		Encrypted text or the text if an error occurs
	 */
	public static String md5(String text)
	{
		String response;
		MessageDigest m;
		
		
		try {
			m = MessageDigest.getInstance("MD5");
			m.update(text.getBytes(),0,text.length());
			response = new BigInteger(1,m.digest()).toString(16);
		} catch (NoSuchAlgorithmException e) {
			response = text;
		}
		
		return response;
	}
	
	/**
	 * Generates a unique variable name. It will be:<br />
	 * <code>MD5(current_time+random_number)</code>
	 * 
	 * @return		Variable name
	 */
	public static String generateVarName()
	{
		return "_"+md5(String.valueOf(new Date().getTime()+(Math.random()*9999+1)));
	}
	
	/**
	 * Converts a path list to string using a delimiter. If path list is empty,
	 * returns '.'.
	 * 
	 * @param		pathList Path list
	 * @param		delimiter Symbol that will be used to separate paths
	 * @param		base All paths in the path list will be relativized with
	 * the base
	 * 
	 * @return		String with paths separated by the specified delimiter or
	 * '.' if there are no paths
	 */
	public static String pathListToString(List<Path> pathList, String delimiter, Path base, boolean onlyDir)
	{
		StringBuilder response = new StringBuilder();
		Set<String> addedPaths = new HashSet<>();
		String currentPath;
		
		
		for (Path dependency : pathList) {
			if (onlyDir) {
				currentPath = dependency.getParent().toString();
				
				if (!addedPaths.contains(currentPath)) {
					addedPaths.add(currentPath);
					response.append(currentPath + "\\*");
					response.append(";");
				}
			}
			else {
				response.append(base.relativize(dependency));
				response.append(";");
			}
		}
		
		if (response.length() > 1)
			response.deleteCharAt(response.length()-1);
		else
			response.append(".");
		
		return response.toString();
	}
	
	/**
	 * Converts a path list to string using a delimiter. If path list is empty,
	 * returns '.'.
	 * 
	 * @param		pathList Path list
	 * @param		delimiter Symbol that will be used to separate paths
	 * 
	 * @return		String with paths separated by the specified delimiter or
	 * '.' if there are no paths
	 */
	public static String pathListToString(List<Path> pathList, String delimiter, boolean onlyDir)
	{
		StringBuilder response = new StringBuilder();
		Set<String> addedPaths = new HashSet<>();
		String currentPath;
		
		
		for (Path dependency : pathList) {
			if (onlyDir) {
				currentPath = dependency.getParent().toString();
				
				if (!addedPaths.contains(currentPath)) {
					addedPaths.add(currentPath);
					response.append(currentPath + "\\*");
					response.append(";");
				}
			}
			else {
				response.append(dependency);
				response.append(";");
			}
		}
		
		if (response.length() > 1)
			response.deleteCharAt(response.length()-1);
		else
			response.append(".");
		
		return response.toString();
	}
	
	/**
	 * Copies files to a directory;
	 * 
	 * @param		files Files to be copied
	 * @param		output Destination directory
	 * 
	 * @throws		IOException If a failure occurs during copying
	 */
	public static void putFilesInFolder(List<Path> files, Path output) throws IOException
	{
		if (!Files.exists(output)) {
			Files.createDirectories(output);
		}
		
		for (Path p : files) {
			Path target = output.resolve(p.getFileName());
			
			
			if (Files.notExists(target))
				Files.copy(p, target);
		}
	}
}
