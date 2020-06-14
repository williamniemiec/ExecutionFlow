package executionFlow.info;

import static java.lang.invoke.MethodType.methodType;

import java.lang.invoke.MethodType;
import java.nio.file.Path;
import java.util.Arrays;


/**
 * Stores information about a method.
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		1.5
 * @since		1.0
 */
public class ClassMethodInfo 
{
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	private String methodName;
	private Path classPath;
	private Path srcPath;
	private String methodSignature;
	private String classSignature;
	private String classPackage;
	private int invocationLine;
	private Class<?>[] parameterTypes;
	private Object[] args;
	private Class<?> returnType;
	
	
	//-------------------------------------------------------------------------
	//		Constructor
	//-------------------------------------------------------------------------
	/**
	 * Stores information about a method.
	 * 
	 * @param		classPath Method class file path
	 * @param		srcPath Path where source file is
	 * @param		invocationLine Line of test method where method is called
	 * @param		methodSignature Signature of the method
	 * @param		methodName Method's name
	 * @param		returnType Return type of the method
	 * @param		parameterTypes Types of method's parameters
	 * @param		args Method's arguments
	 */
	private ClassMethodInfo(Path classPath, Path srcPath, int invocationLine,
			String methodSignature, String methodName,
			Class<?> returnType, Class<?>[] parameterTypes, Object[] args) 
	{
		this.classPath = classPath;
		this.srcPath = srcPath;
		this.methodSignature = methodSignature;
		this.methodName = methodName;
		this.returnType = returnType;
		this.parameterTypes = parameterTypes;
		this.args = args;
		this.invocationLine = invocationLine;
	}

	
	//-------------------------------------------------------------------------
	//		Builder
	//-------------------------------------------------------------------------
	/**
	 * Builder for ClassMethodInfo. It is necessary to fill all required 
	 * fields. The required fields are: <br />
	 * <ul>
	 * 	<li>classPath</li>
	 * 	<li>srcPath</li>
	 * 	<li>methodSignature</li>
	 * </ul>
	 */
	public static class ClassMethodInfoBuilder
	{
		private String methodName;
		private Path classPath;
		private Path srcPath;
		private String methodSignature;
		private int invocationLine;
		private Class<?>[] parameterTypes;
		private Object[] args;
		private Class<?> returnType = void.class;
		
		
		/**
		 * @param		methodName Method's name
		 * @return		Builder to allow chained calls
		 * @throws		IllegalArgumentException If methodName is null
		 */
		public ClassMethodInfoBuilder methodName(String methodName)
		{
			if (methodName == null)
				throw new IllegalArgumentException("Method's name cannot be null");
			
			this.methodName = methodName;
			
			return this;
		}
		
		/**
		 * @param		classPath Method class file path
		 * @return		Builder to allow chained calls
		 * @throws		IllegalArgumentException If classPath is null
		 */
		public ClassMethodInfoBuilder classPath(Path classPath)
		{
			if (classPath == null)
				throw new IllegalArgumentException("Method class file path cannot be null");
			
			this.classPath = classPath.isAbsolute() ? classPath : classPath.toAbsolutePath();
			
			return this;
		}
		
		/**
		 * @param		srcPath Path where method's source file is
		 * @return		Builder to allow chained calls
		 * @throws		IllegalArgumentException If srcPath is null
		 */
		public ClassMethodInfoBuilder srcPath(Path srcPath)
		{
			if (srcPath == null)
				throw new IllegalArgumentException("Method's source file cannot be null");
			
			this.srcPath = srcPath.isAbsolute() ? srcPath : srcPath.toAbsolutePath();
			
			return this;
		}
		
		/**
		 * @param		methodSignature Method signature
		 * @return		Builder to allow chained calls
		 * @throws		IllegalArgumentException If methodSignature is null
		 */
		public ClassMethodInfoBuilder methodSignature(String methodSignature)
		{
			if (methodSignature == null)
				throw new IllegalArgumentException("Method signature cannot be null");
			
			this.methodSignature = methodSignature;
			
			return this;
		}
		
		/**
		 * @param		invocationLine Line of test method where method is 
		 * called
		 * @return		Builder to allow chained calls
		 * @throws		IllegalArgumentException If invocationLine is less than
		 * or equal to zero
		 */
		public ClassMethodInfoBuilder invocationLine(int invocationLine)
		{
			if (invocationLine <= 0)
				throw new IllegalArgumentException("Invocation line must be a number greater than zero");
			
			this.invocationLine = invocationLine;
			
			return this;
		}
		
		/**
		 * @param		parameterTypes Types of method's parameters
		 * @return		Builder to allow chained calls
		 * @throws		IllegalArgumentException If parameterTypes is null
		 */
		public ClassMethodInfoBuilder parameterTypes(Class<?>[] parameterTypes)
		{
			if (parameterTypes == null)
				throw new IllegalArgumentException("Types of method's parameters cannot be null");
			
			this.parameterTypes = parameterTypes;
			
			return this;
		}
		
		/**
		 * @param		args Method's arguments
		 * @return		Builder to allow chained calls
		 * @throws		IllegalArgumentException If args is null
		 */
		public ClassMethodInfoBuilder args(Object... args)
		{
			if (args == null)
				throw new IllegalArgumentException("Method's arguments cannot be null");
			
			this.args = args;
			
			return this;
		}

		/**
		 * @param		returnType Method return type
		 * @return		Builder to allow chained calls
		 * @throws		IllegalArgumentException If returnType is null
		 */
		public ClassMethodInfoBuilder returnType(Class<?> returnType)
		{
			if (returnType == null)
				throw new IllegalArgumentException("Method return type cannot be null");
			
			this.returnType = returnType;
			
			return this;
		}
		
		/**
		 * Creates {@link ClassMethodInfo} with provided information. It is
		 * necessary that required fields must be filled. The required 
		 * fields are: <br />
		 * <ul>
		 * 	<li>classPath</li>
		 * 	<li>srcPath</li>
		 * 	<li>methodSignature</li>
		 * </ul>
		 * 
		 * @return		ClassMethodInfo with provided information
		 * @throws		IllegalArgumentException If any required field is null
		 */
		public ClassMethodInfo build() throws IllegalArgumentException
		{
			StringBuilder nullFields = new StringBuilder();
			
			if (classPath == null)
				nullFields.append("classPath").append(", ");
			if (srcPath == null)
				nullFields.append("srcPath").append(", ");
			if (methodSignature == null)
				nullFields.append("methodSignature").append(", ");
			
			if (nullFields.length() > 0)
				throw new IllegalArgumentException("Required fields cannot be null: "
						+ nullFields.substring(0, nullFields.length()-2));	// Removes last comma
			
			return new ClassMethodInfo(
				classPath, srcPath, invocationLine, methodSignature, 
				methodName, returnType, parameterTypes, args
			);
		}
	}
	
	
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	@Override
	public String toString() 
	{
		return "ClassMethodInfo [methodName=" + methodName 
				+ ", classPath=" + classPath + ", srcPath=" + srcPath
				+ ", classSignature=" + classSignature 
				+ ", methodSignature=" + methodSignature 
				+ ", invocationLine=" + invocationLine 
				+ ", parameterTypes=" + Arrays.toString(parameterTypes) 
				+ ", args="	+ Arrays.toString(args) 
				+ ", returnType=" + returnType + "]";
	}
	
	/**
	 * Extracts the types of the method parameters.
	 * 
	 * @param		parametersTypes Types of each method's parameter
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
	 * Extracts class signature from a method signature.
	 * 
	 * @param		methodSignature Signature of the method
	 * @return		Class signature
	 */
	public static String extractClassSignature(String methodSignature)
	{
		StringBuilder response = new StringBuilder();
		String[] terms = methodSignature.split("\\.");
		
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
	
	
	//-------------------------------------------------------------------------
	//		Getters
	//-------------------------------------------------------------------------
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
	 * Gets path of the compiled file of the method.
	 * 
	 * @return		Path of the compiled file of the method
	 */
	public Path getClassPath()
	{
		return this.classPath;
	}
	
	/**
	 * Gets the path of the method source file.
	 * 
	 * @return		Path of the method source file
	 */
	public Path getSrcPath()
	{
		return this.srcPath;
	}
	
	/**
	 * Gets method's signature.
	 * 
	 * @return		Method signature
	 */
	public String getMethodSignature()
	{
		return this.methodSignature;
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
			classSignature = extractClassSignature(methodSignature);
		
		return classSignature; 
	}
	
	/**
	 * Gets line where the method is invoked in the test method.
	 * 
	 * @return		Line where the method is invoked in the test method
	 */
	public int getInvocationLine()
	{
		return this.invocationLine;
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
	 * Gets values from the method's arguments.
	 * 
	 * @return		Values from the method's arguments
	 */
	public Object[] getArgs() 
	{
		return args;
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
	 * Gets package of the method.
	 * 
	 * @return		Package to which the class belongs
	 * @implNote	Lazy initialization
	 */
	public String getPackage()
	{
		if (classPackage == null)
			classPackage = extractPackage(getClassSignature());

		return classPackage;
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
	 * Gets directory where the method's compiled file is.
	 * 
	 * @return		Directory where the method's source file is.
	 */
	public Path getClassDirectory()
	{
		return classPath.getParent();
	}
	
	/**
	 * Gets directory where the method's source file is.
	 * 
	 * @return		Directory where the method's source file is.
	 */
	public Path getSrcDirectory()
	{
		return srcPath.getParent();
	}
}