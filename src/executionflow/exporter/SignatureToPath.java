package executionflow.exporter;

import util.io.path.ReservedCharactersReplacer;

/**
 * Path generator based on method or constructor signatures .
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		6.0.5
 * @since		6.0.5
 */
public class SignatureToPath {
	
	//-------------------------------------------------------------------------
	//		Constructor
	//-------------------------------------------------------------------------
	private SignatureToPath() {
	}
	
	
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
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
		
		return folderPath + "/" + ReservedCharactersReplacer.replaceReservedCharacters(folderName);
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
		
		return ReservedCharactersReplacer.replaceReservedCharacters(folderName);
	}
}
