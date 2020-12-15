package executionFlow.info;

import static java.lang.invoke.MethodType.methodType;

import java.lang.invoke.MethodType;
import java.nio.file.Path;
import java.util.Arrays;


/**
 * Stores information about an invoked, where an invoked can be a 
 * method or a constructor. 
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		5.2.3
 * @since		2.0.0
 */
public class InvokedInfo 
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
	private String invokedName;
	private String name;
	private String concreteMethodSignature;
	private String methodName;
	private Class<?> returnType;
	
	
	private InvokedInfo(Path binPath, Path srcPath, int invocationLine,
			String methodSignature, String methodName,
			Class<?> returnType, Class<?>[] parameterTypes, Object[] args,
			boolean isConstructor) 
	{
		this.binPath = binPath;
		this.srcPath = srcPath;
		this.invocationLine = invocationLine;
		this.invokedSignature = methodSignature;
		this.methodName = methodName;
		this.returnType = returnType;
		this.parameterTypes = parameterTypes;
		this.args = args;
		
		if (isConstructor)
			this.classSignature = invokedSignature;
	}
	
	
	//-------------------------------------------------------------------------
	//		Builder
	//-------------------------------------------------------------------------
	/**
	 * Builder for {@link InvokedInfo}. It is necessary to provide all required
	 * fields. The required fields are: <br />
	 * <ul>
	 * 	<li>binPath</li>
	 * 	<li>srcPath</li>
	 * 	<li>invokedSignature</li>
	 * </ul>
	 */
	public static class Builder
	{
		private String invokedName;
		private Path binPath;
		private Path srcPath;
		private String invokedSignature;
		private int invocationLine;
		private Class<?>[] parameterTypes;
		private Object[] args;
		private Class<?> returnType = void.class;
		private boolean isConstructor = false;
		
		
		/**
		 * @param		invokedName Invoked's name
		 * 
		 * @return		Builder to allow chained calls
		 * 
		 * @throws		IllegalArgumentException If methodName is null
		 */
		public Builder invokedName(String name)
		{
			if (name == null)
				throw new IllegalArgumentException("Invoked name cannot be null");
			
			this.invokedName = name.trim();
			
			return this;
		}
		
		/**
		 * @param		binPath Path where invoked's compiled file is
		 * 
		 * @return		Builder to allow chained calls
		 * 
		 * @throws		IllegalArgumentException If binPath is null
		 */
		public Builder binPath(Path binPath)
		{
			if (binPath == null)
				throw new IllegalArgumentException("Invoked's compiled file path cannot be null");
			
			this.binPath = binPath.isAbsolute() ? binPath : binPath.toAbsolutePath();
			
			return this;
		}
		
		/**
		 * @param		srcPath Path where invoked's source file is
		 * 
		 * @return		Builder to allow chained calls
		 * 
		 * @throws		IllegalArgumentException If srcPath is null
		 */
		public Builder srcPath(Path srcPath)
		{
			if (srcPath == null)
				throw new IllegalArgumentException("Invoked's source file cannot be null");
			
			this.srcPath = srcPath.isAbsolute() ? srcPath : srcPath.toAbsolutePath();
			
			return this;
		}
		
		/**
		 * @param		invokedSignature Invoked signature (if it is an
		 * inner class method, there must be '$' between the class name and
		 * the inner class name).
		 * <h5>Example</h5>
		 * <ul>
		 * 	<li><b>Method name along with its parameters:</b> firstName(String)</li>
		 * 	<li><b>Class name:</b> Person</li>
		 * 	<li><b>Inner class name:</b> PersonBuilder</li>
		 * 	<li><b>Class package:</b> examples.builderPattern</li>
		 * 	<li><b>Inner class method signature:</b> examples.builderPattern.Person$PersonBuilder.firstName(String)</li>
		 * </ul>
		 * 
		 * @return		Builder to allow chained calls
		 * 
		 * @throws		IllegalArgumentException If methodSignature is null
		 */
		public Builder invokedSignature(String signature)
		{
			if (signature == null)
				throw new IllegalArgumentException("Invoked signature cannot be null");
			
			this.invokedSignature = signature.trim();
			
			return this;
		}
		
		/**
		 * @param		invocationLine Line of test method where method is 
		 * called
		 * 
		 * @return		Builder to allow chained calls
		 * 
		 * @throws		IllegalArgumentException If invocationLine is less than
		 * or equal to zero
		 */
		public Builder invocationLine(int invocationLine)
		{
			if (invocationLine <= 0)
				throw new IllegalArgumentException("Invocation line must be a number greater than zero");
			
			this.invocationLine = invocationLine;
			
			return this;
		}
		
		public Builder isConstructor(boolean isConstructor)
		{
			this.isConstructor = isConstructor;
			
			return this;
		}
		
		/**
		 * @param		parameterTypes Types of invoked's parameters
		 * 
		 * @return		Builder to allow chained calls
		 * 
		 * @throws		IllegalArgumentException If parameterTypes is null
		 */
		public Builder parameterTypes(Class<?>[] parameterTypes)
		{
			if (parameterTypes == null)
				throw new IllegalArgumentException("Types of invoked's parameters cannot be null");
			
			this.parameterTypes = parameterTypes;
			
			return this;
		}
		
		/**
		 * @param		args Invoked's arguments
		 * 
		 * @return		Builder to allow chained calls
		 * 
		 * @throws		IllegalArgumentException If args is null
		 */
		public Builder args(Object... args)
		{
			if (args == null)
				throw new IllegalArgumentException("Invoked's arguments cannot be null");
			
			this.args = args;
			
			return this;
		}

		/**
		 * @param		returnType Invoked return type
		 * 
		 * @return		Builder to allow chained calls
		 * 
		 * @throws		IllegalArgumentException If returnType is null
		 */
		public Builder returnType(Class<?> returnType)
		{
			if (returnType == null)
				throw new IllegalArgumentException("Invoked return type cannot be null");
			
			this.returnType = returnType;
			
			return this;
		}
		
		/**
		 * Creates {@link InvokedInfo} with provided information. It is 
		 * necessary to provide all required fields.. The required fields 
		 * are: <br />
		 * <ul>
		 * 	<li>classPath</li>
		 * 	<li>srcPath</li>
		 * 	<li>invokedSignature</li>
		 * </ul>
		 * 
		 * @return		InvokedInfo with provided information
		 * 
		 * @throws		IllegalArgumentException If any required field is null
		 */
		public InvokedInfo build() throws IllegalArgumentException
		{
			StringBuilder nullFields = new StringBuilder();
			
			
			if (binPath == null)
				nullFields.append("classPath").append(", ");
			if (srcPath == null)
				nullFields.append("srcPath").append(", ");
			if (invokedSignature == null)
				nullFields.append("invokedSignature").append(", ");
			
			if (nullFields.length() > 0)
				throw new IllegalArgumentException("Required fields cannot be null: "
						+ nullFields.substring(0, nullFields.length()-2));	// Removes last comma
			
			return new InvokedInfo(
				binPath, srcPath, invocationLine, invokedSignature, 
				invokedName, returnType, parameterTypes, args, isConstructor
			);
		}
	}
	
	
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
	
	public String getName()
	{
		if (name == null)
			extractName();
		
		return name;
	}

	private void extractName() {
		this.name = invokedSignature.substring(0, invokedSignature.indexOf("(")+1);
	}

	public String getInvokedName() {
		if (invokedName == null)
			extractInvokedName();
		
		return invokedName;
	}

	private void extractInvokedName() {
		int idxParamStart = invokedSignature.indexOf("(");
		
		invokedName = invokedSignature.substring(0, idxParamStart);
		invokedName = invokedName.substring(invokedName.lastIndexOf("."), idxParamStart);
	}
	
	@Override
	public String toString() 
	{
		return "InvokedInfo ["
				+ "methodName=" + methodName 
				+ ", binPath=" + binPath 
				+ ", srcPath=" + srcPath
				+ ", classSignature=" + getClassSignature()
				+ ", classPackage=" + getPackage()
				+ ", methodSignature=" + invokedSignature 
				+ ", invocationLine=" + invocationLine 
				+ ", parameterTypes=" + Arrays.toString(parameterTypes) 
				+ ", args="	+ Arrays.toString(args) 
				+ ", returnType=" + returnType 
			+ "]";
	}
	
	//-------------------------------------------------------------------------
	//		Getters
	//-------------------------------------------------------------------------
	public String getMethodSignature()
	{
		return getInvokedSignature();
	}
	
	/**
	 * Gets method name.
	 * 
	 * @return		Method name
	 */
	public String getMethodName() 
	{
		return methodName;
	}

	/**
	 * Gets types from the method's parameters.
	 * 
	 * @return		Method parameters
	 */
	public Class<?>[] getParameterTypes() 
	{
		return parameterTypes;
	}
	
	/**
	 * Gets return type of the method.
	 * 
	 * @return		Method return type
	 */
	public Class<?> getReturnType()
	{
		return this.returnType;
	}
	
	/**
	 * Gets parameter types and return type of the method.
	 * 
	 * @return		Return type and parameter types of the method
	 */
	public MethodType getMethodTypes() 
	{
		if (args == null || args.length == 0)
			return methodType(returnType);
		
		return methodType(returnType, parameterTypes);
	}
	
	public String getConcreteInvokedSignature()
	{
		return (concreteMethodSignature == null) ? 
				invokedSignature.replaceAll("\\$", ".") 
				: concreteMethodSignature.replaceAll("\\$", ".");
	}
	
	public void setConcreteMethodSignature(String concreteMethodSignature) 
	{
		this.concreteMethodSignature = concreteMethodSignature;
	}
	
	/**
	 * Sets invoked signature.
	 * 
	 * @param		signature New signature
	 * 
	 * @return		Itself to allow chained calls
	 */
	public InvokedInfo setInvokedSignature(String signature)
	{
		this.invokedSignature = signature;
		
		return this;
	}
}
