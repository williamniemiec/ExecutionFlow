package executionFlow.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * Contains methods that perform data manipulation.
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		1.5
 * @since		1.5
 */
public class DataUtils 
{
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
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
	 * Generates a path based on an invoker signature with the following format:
	 * <ul>
	 * 	<li><b>Invoker = method:</b> <code>package/className.methodName(parameterTypes)</code></li>
	 * 	<li><b>Invoker = constructor:</b> <code>package/className(parameterTypes)</code></li>
	 * </ul>
	 * 
	 * <h1>Example</h1>
	 * <ul>
	 * 	<li><b>Invoker signature (method):</b> controlFlow.TestClass_ControlFlow.ifElseMethod(int)</li>
	 * 	<li><b>Generated path:</b> controlFlow/TestClass_ControlFlow.ifElseMethod(int)</li>
	 * </ul>
	 * 
	 * @param		invokerSignature Invoker signature
	 * @param		isConstructor If the invoker is a constructor
	 * 
	 * @return		Generated path
	 */
	public static String getSavePath(String invokerSignature, boolean isConstructor)
	{
		String[] signatureFields = invokerSignature.split("\\.");
		String folderPath = getFolderPath(signatureFields, isConstructor);
		String folderName = getFolderName(signatureFields, isConstructor);
		
		
		return folderPath+"/"+folderName;
	}
	
	/**
	 * Generates folder's path based on invoker's signature. It will generates
	 * path following the package of this invoker.
	 * 
	 * <h1>Format</h1>
	 * <ul>
	 * 	<li><b>Invoker signature (method):</b> package1.package2.package3.className.methodName(parameter types)</li>
	 * 	<li><b>Folder path:</b> package1/packag2/package3</li>
	 * </ul>
	 * 
	 * @param		signatureFields Fields of the invoker signature
	 * @param		isConstructor If the invoker is a constructor
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
	 * Generates folder's name based on invoker's signature. This name will be:
	 * <code>className.invokerName(parameter types)</code>
	 * 
	 * @param		signatureFields Fields of the invoker signature
	 * 
	 * @return		Folder's name
	 */
	private static String getFolderName(String[] signatureFields, boolean isConstructor)
	{
		String response = null;
		
		
		if (isConstructor) {
			// Extracts class name
			String className = signatureFields[signatureFields.length-1];
			
			response = className;
		}
		else {
			// Extracts class name
			String className = signatureFields[signatureFields.length-2];
			
			// Extracts invoker name with parameter types
			String invokerName = signatureFields[signatureFields.length-1];
			
			response = className+"."+invokerName;
		}
		
		return response;
	}
}
