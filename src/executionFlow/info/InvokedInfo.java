package executionFlow.info;

import java.nio.file.Path;


/**
 * Stores information about an invoked, where an invoked can be a 
 * method or a constructor. 
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		3.2.0
 * @since		2.0.0
 */
public abstract class InvokedInfo 
{
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	protected Path binPath;
	protected Path srcPath;
	protected String invokedSignature;
	protected String classSignature;
	protected String classPackage;
	protected int invocationLine;
	protected Class<?>[] parameterTypes;
	protected Object[] args;
	
	
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	/**
	 * Extracts the types of parameters from the invoked.
	 * 
	 * @param		parametersTypes Types of each invoked's parameter
	 * 
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
	 * Extracts class signature from an invoked signature.
	 * 
	 * @param		invokedSignature Invoked signature
	 * 
	 * @return		Class signature
	 */
	public static String extractClassSignature(String invokedSignature)
	{
		StringBuilder response = new StringBuilder();
		String[] terms = invokedSignature.split("\\.");

		
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
	 * Gets invoked signature without return type.
	 * 
	 * @param		invokedSignature Invoked signature
	 * 
	 * @return		Invoked signature without return type
	 */
	public static String getInvokedSignatureWithoutReturnType(String invokedSignature)
	{
		int index = invokedSignature.indexOf(" ");
		
		
		return invokedSignature.substring(index+1);
	}
	
	/**
	 * Checks whether the invoked belongs to an anonymous class.
	 * 
	 * @return		If the invoked belongs to an anonymous class
	 */
	public boolean belongsToAnonymousClass()
	{
		String sig = getClassSignature();
		String[] terms = sig.split("\\$");
		boolean response = false;
		
		
		if (terms.length > 1) {
			response = terms[terms.length-1].matches("[0-9]+");
		}

		return response;
	}
	
	
	//-------------------------------------------------------------------------
	//		Getters
	//-------------------------------------------------------------------------
	/**
	 * Gets directory where a compiled file is.
	 * 
	 * @param		compiledFilePath Compiled file path
	 * 
	 * @return		Directory where the compiled test method file is
	 */
	public static Path getCompiledFileDirectory(Path compiledFilePath)
	{
		return compiledFilePath.getParent();
	}
	
	/**
	 * Gets compiled file path.
	 * 
	 * @return		Compiled file path
	 */
	public Path getBinPath()
	{
		return this.binPath;
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
	 * Gets invoked's signature.
	 * 
	 * @return		Invoked signature
	 */
	public String getInvokedSignature()
	{
		return this.invokedSignature;
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
			classSignature = extractClassSignature(invokedSignature);
		
		return classSignature; 
	}
	
	/**
	 * Gets line where the invoked is called in the test method.
	 * 
	 * @return		Line where the invoked is called in the test method
	 */
	public int getInvocationLine()
	{
		return this.invocationLine;
	}
	
	/**
	 * Sets invocation line.
	 * 
	 * @param		line Invocation line (must be greater than zero)
	 * 
	 * @return		Itself to allow chained calls
	 */
	public InvokedInfo setInvocationLine(int line) {
		if (line > 0) {
			this.invocationLine = line;
		}
		
		return this;
	}
	
	/**
	 * Gets package of the invoked.
	 * 
	 * @return		Package to which the invoked's class belongs
	 * 
	 * @implNote	Lazy initialization
	 */
	public String getPackage()
	{
		if (classPackage == null)
			classPackage = extractPackage(getClassSignature());

		return classPackage;
	}
	
	/**
	 * Gets directory where the invoked's compiled file is.
	 * 
	 * @return		Directory where the invoked's source file is.
	 */
	public Path getClassDirectory()
	{
		return binPath.getParent();
	}
	
	/**
	 * Gets directory where the invoked's source file is.
	 * 
	 * @return		Directory where the invoked's source file is.
	 */
	public Path getSrcDirectory()
	{
		return srcPath.getParent();
	}
	
	/**
	 * Gets values from the invoked's arguments.
	 * 
	 * @return		Values from the invoked's arguments
	 */
	public Object[] getArgs() 
	{
		return args;
	}
}
