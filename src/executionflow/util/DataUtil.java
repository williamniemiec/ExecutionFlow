package executionflow.util;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Contains methods that perform data manipulation.
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 */
public class DataUtil {
	
	//-------------------------------------------------------------------------
	//		Constructor
	//-------------------------------------------------------------------------
	private DataUtil() {
	}
	
	
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
	public static <T> String implode(List<T> list, String delimiter) {
		StringBuilder response = new StringBuilder();
		
		for (T p : list) {
			response.append(p);
			response.append(delimiter);
		}
		
		// Removes last delimiter
		if (response.length() > 1) {
			response.deleteCharAt(response.length()-1);
		}
		
		return response.toString();
	}
	
	/**
	 * Given two Maps, adds all content from the first Map to the second.
	 * 
	 * @param		source Some map
	 * @param		target Map that will be merge with map1 
	 */
	public static void mergesMaps(Map<String, List<String>> source, 
								  Map<String, List<String>> target) {
		if ((source == null) || source.isEmpty())
			return;
		
		for (Map.Entry<String, List<String>> e : source.entrySet()) {
			String keyMap1 = e.getKey();

			// Adds content from first Map to the second
			for (String contentMap1 : e.getValue()) {
				List<String> contentMap2;
				// If second Map contains the same key as the first, add all
				// the content of this key from first Map in the second
				if (target.containsKey(keyMap1)) {
					contentMap2 = target.get(keyMap1);
					
					if (!contentMap2.contains(contentMap1)) {
						contentMap2.add(contentMap1);
					}
				}
				else {
					contentMap2 = new ArrayList<>();
					contentMap2.add(contentMap1);
					target.put(keyMap1, contentMap2);
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
	public static String generateDirectoryPathFromSignature(String invokedSignature, 
															boolean isConstructor) {
		String[] signatureFields = extractSignatureFields(invokedSignature);
		
		String folderPath = getFolderPath(signatureFields, isConstructor);
		String folderName = getFolderName(signatureFields, isConstructor) 
				+ reduceSignature(extractParametersFromSignature(invokedSignature));
		
		return folderPath + "/" + FileUtil.replaceReservedCharacters(folderName);
	}
	
	private static String[] extractSignatureFields(String signature) {
		String signatureWithoutParameters = signature.contains("(")
				? signature.substring(0, signature.indexOf("("))
				: signature;
				
		return	signatureWithoutParameters.split("\\.");
	}	
	
	private static String reduceSignature(String parameters) {
		return parameters.replaceAll("([^.(\\s]+\\.)+", "");
	}
	
	private static String extractParametersFromSignature(String signature) {
		if (!signature.contains("("))
			return "()";
		
		return signature.substring(signature.indexOf("("));
	}
	
	/**
	 * Generates folder's path based on invoked's signature. It will generates
	 * path following the package of this invoked.
	 * 
	 * <h1>Format</h1>
	 * <ul>
	 * 	<li><b>Invoked signature (method):</b> 
	 * 	package1.package2.package3.className.methodName(parameter types)</li>
	 * 	<li><b>Folder path:</b> package1/packag2/package3</li>
	 * </ul>
	 * 
	 * @param		signatureFields Fields of the invoked signature
	 * @param		isConstructor If the invoked is a constructor
	 * 
	 * @return		Folder's path
	 */
	private static String getFolderPath(String[] signatureFields, boolean isConstructor) {
		StringBuilder folderPath = new StringBuilder();
		int size = isConstructor ? signatureFields.length-1 : signatureFields.length-2;
		
		for (int i=0; i<size; i++) {
			folderPath.append(signatureFields[i]);
			folderPath.append("/");
		}
		
		// Removes last slash
		if (folderPath.length() > 0) {
			folderPath.deleteCharAt(folderPath.length()-1);	
		}
		
		return folderPath.toString();
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
	private static String getFolderName(String[] signatureFields, boolean isConstructor) {
		String folderName = "";
		
		if (isConstructor) {
			String className = signatureFields[signatureFields.length-1];
			
			folderName = className;
		}
		else {			
			String className = signatureFields[signatureFields.length-2];
			String methodName = signatureFields[signatureFields.length-1];
			
			folderName = className + "." + methodName;
		}
		
		return FileUtil.replaceReservedCharacters(folderName);
	}
	
	/**
	 * Encrypts a text in MD5.
	 * 
	 * @param		text Text to be encrypted
	 * 
	 * @return		Encrypted text or the text if an error occurs
	 */
	public static String md5(String text) {
		String md5Text;
		
		try {
			MessageDigest m = MessageDigest.getInstance("MD5");
			m.update(text.getBytes(), 0, text.length());
			
			md5Text = new BigInteger(1, m.digest()).toString(16);
		} 
		catch (NoSuchAlgorithmException e) {
			md5Text = text;
		}
		
		return md5Text;
	}
	
	/**
	 * Generates a unique variable name. It will be:<br />
	 * <code>MD5(current_time+random_number)</code>
	 * 
	 * @return		Variable name
	 */
	public static String generateVarName() {
		return ("_" + md5(String.valueOf(generateRandomNumber())));
	}
	
	private static double generateRandomNumber() {
		return (new Date().getTime() + (Math.random() * 9999 + 1));
	}
	
	/**
	 * Gets indentation of a line.
	 * 
	 * @param		line Line to be taken indentation
	 * 
	 * @return		Indentation or empty string if there is no indentation
	 */
	public static String getIndentation(String line) {
		final String regexIndent = "^(\\ |\\t)+";
		Matcher m = Pattern.compile(regexIndent).matcher(line);
		
		if (!m.find())
			return "";
		
		return m.group();
	}
	
	public static String extractContentBetweenParenthesis(String content) {
		Pattern patternContentInParenthesis = Pattern.compile("\\(.*\\)");
		Matcher contentBetweenParenthesis = patternContentInParenthesis.matcher(content);
		
		if (!contentBetweenParenthesis.find())
			return "";
		
		return contentBetweenParenthesis.group().replace("(", "").replace(")", "");
	}
}
