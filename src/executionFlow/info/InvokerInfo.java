package executionFlow.info;

import java.nio.file.Path;


/**
 * Stores information about an invoker, where an invoker can be a 
 * method or a constructor. 
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		1.5
 * @since		1.5
 */
public abstract class InvokerInfo 
{
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	protected Path classPath;
	protected Path srcPath;
	protected String invokerSignature;
	protected String classSignature;
	protected String classPackage;
	protected int invocationLine;
	protected Class<?>[] parameterTypes;
	protected Object[] args;
	
	
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	/**
	 * Extracts the types of parameters from the invoker.
	 * 
	 * @param		parametersTypes Types of each invoker's parameter
	 * @return		String with the name of each type separated by commas
	 */
	public String extractParameterTypes()
	{
		if (parameterTypes == null) { return ""; }
		StringBuilder response = new StringBuilder();
		
		// Stores the name of all types of parameter
		for (var parameterType : parameterTypes) {
			// Removes signature
			String[] tmp = parameterType.getTypeName().split("\\."); 
			
			// Adds only the name of the parameter type
			response.append(tmp[tmp.length-1] +",");
		}
		
		if (response.length() > 0)
			// Removes last comma
			response.deleteCharAt(response.length()-1);	
		
		return response.toString();
	}
	
	/**
	 * Extracts class signature from a invoker signature.
	 * 
	 * @param		invokerSignature Signature of the invoker
	 * @return		Class signature
	 */
	public static String extractClassSignature(String invokerSignature)
	{
		StringBuilder response = new StringBuilder();
		String[] terms = invokerSignature.split("\\.");

		
		// Appends all terms of signature, without the last
		for (int i=0; i<terms.length-1; i++) {
			response.append(terms[i]);
			response.append(".");
		}
		
		if (response.length() > 0) {
			// Removes last dot
			response.deleteCharAt(response.length()-1);
		}
		
		return response.toString();
	}
	
	/**
	 * Extracts package from a class signature.
	 * 
	 * @param		classSignature Signature of the class
	 * @return		Class package
	 */
	public static String extractPackage(String classSignature)
	{
		if (classSignature == null || classSignature.isEmpty()) { return ""; }
		
		String[] tmp = classSignature.split("\\.");
		StringBuilder response = new StringBuilder();
		
		// Appends all terms of signature, without the last
		for (int i=0; i<tmp.length-1; i++) {
			response.append(tmp[i]);
			response.append(".");
		}
		
		if (response.length() > 0) {
			// Removes last dot
			response.deleteCharAt(response.length()-1);
		}
		
		return response.toString();
	}
	
	/**
	 * Extracts class root directory. <br />
	 * Example: <br />
	 * <li><b>Class path:</b> C:/app/bin/packageName1/packageName2/className.java</li>
	 * <li><b>Class root directory:</b> C:/app/bin</li>
	 * 
	 * @param		classPath Path where compiled file is
	 * @param		classPackage Package of this class
	 * @return		Class root directory
	 */
	public static Path extractClassRootDirectory(Path classPath, String classPackage)
	{
		int packageFolders = classPackage.isEmpty() || classPackage == null ? 
				0 : classPackage.split("\\.").length;

		classPath = classPath.getParent();
		
		// Sets path to the compiler
		for (int i=0; i<packageFolders; i++) {
			classPath = classPath.getParent();
		}
		
		return classPath;
	}
	
	/**
	 * Gets invoker signature without return type.
	 * 
	 * @param		invokerSignature Invoker signature
	 * 
	 * @return		Invoker signature without return type
	 */
	public static String getInvokerSignatureWithoutReturnType(String invokerSignature)
	{
		int index = invokerSignature.indexOf(" ");
		
		
		return invokerSignature.substring(index+1);
	}
	
	
	//-------------------------------------------------------------------------
	//		Getters
	//-------------------------------------------------------------------------
	/**
	 * Gets the compiled file path.
	 * 
	 * @return		Compiled file path
	 */
	public Path getClassPath()
	{
		return this.classPath;
	}
	
	/**
	 * Gets the the source file path.
	 * 
	 * @return		Source file path
	 */
	public Path getSrcPath()
	{
		return this.srcPath;
	}
	
	/**
	 * Gets invoker's signature.
	 * 
	 * @return		invoker signature
	 */
	public String getInvokerSignature()
	{
		return this.invokerSignature;
	}
	
	/**
	 * Gets class signature. 
	 * 
	 * @return		Class signature
	 * @implNote	Lazy initialization
	 */
	public String getClassSignature()
	{
		if (classSignature == null)
			classSignature = extractClassSignature(invokerSignature);
		
		return classSignature; 
	}
	
	/**
	 * Gets line where the invoker is invoked in the test method.
	 * 
	 * @return		Line where the invoker is invoked in the test method
	 */
	public int getInvocationLine()
	{
		return this.invocationLine;
	}
	
	/**
	 * Gets package of the invoker.
	 * 
	 * @return		Package to which the invoker's class belongs
	 * @implNote	Lazy initialization
	 */
	public String getPackage()
	{
		if (classPackage == null)
			classPackage = extractPackage(getClassSignature());

		return classPackage;
	}

	/**
	 * Gets directory where a compiled file is.
	 * 
	 * @param		compiledFilePath Compiled file path
	 * @return		Directory where the compiled test method file is
	 */
	public static Path getCompiledFileDirectory(Path compiledFilePath)
	{
		return compiledFilePath.getParent();
	}
	
	/**
	 * Gets directory where the invoker's compiled file is.
	 * 
	 * @return		Directory where the invoker's source file is.
	 */
	public Path getClassDirectory()
	{
		return classPath.getParent();
	}
	
	/**
	 * Gets directory where the invoker's source file is.
	 * 
	 * @return		Directory where the invoker's source file is.
	 */
	public Path getSrcDirectory()
	{
		return srcPath.getParent();
	}
	
	/**
	 * Gets values from the invoker's arguments.
	 * 
	 * @return		Values from the invoker's arguments
	 */
	public Object[] getArgs() 
	{
		return args;
	}
}
