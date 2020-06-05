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
	private Path testClassPath;
	private Path srcPath;
	private Path testSrcPath;
	private String testMethodSignature;
	private String methodSignature;
	private String classSignature;
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
	 * @param		testClassPath Test method class file path
	 * @param		srcPath Path where source file is
	 * @param		testSrcPath Path where source file of test method 
	 * is
	 * @param		invocationLine Line of test method where method is called
	 * @param		classSignature Signature of the class that the method 
	 * belongs
	 * @param		methodSignature Signature of the method
	 * @param		testMethodSignature Signature of the test method to which 
	 * the method belongs
	 * @param		methodName Method's name
	 * @param		returnType Return type of the method
	 * @param		parameterTypes Types of method's parameters
	 * @param		args Method's arguments
	 */
	private ClassMethodInfo(Path classPath, Path testClassPath, Path srcPath, 
			Path testSrcPath, int invocationLine, String classSignature,
			String methodSignature, String testMethodSignature, 
			String methodName, Class<?> returnType, Class<?>[] parameterTypes, 
			Object... args) 
	{
		this.classPath = classPath;
		this.testClassPath = testClassPath;
		this.srcPath = srcPath;
		this.testSrcPath = testSrcPath;
		this.methodSignature = methodSignature;
		this.classSignature = classSignature;
		this.testMethodSignature = testMethodSignature;
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
	 * 		<li>classPath</li>
	 * 		<li>testClassPath</li>
	 * 		<li>srcPath</li>
	 * 		<li>testSrcPath</li>
	 * 		<li>invocationLine</li>
	 * 		<li>methodSignature</li>
	 * 		<li>classSignature</li>
	 * 		<li>testMethodSignature</li>
	 * 		<li>methodName</li>
	 * </ul>
	 */
	public static class ClassMethodInfoBuilder
	{
		private String methodName;
		private Path classPath;
		private Path testClassPath;
		private Path srcPath;
		private Path testSrcPath;
		private String classSignature;
		private String methodSignature;
		private String testMethodSignature;
		private int invocationLine;
		private Class<?>[] parameterTypes;
		private Object[] args;
		private Class<?> returnType = void.class;
		
		
		/**
		 * @param		methodName Method's name
		 * @return		Builder to allow chained calls
		 */
		public ClassMethodInfoBuilder methodName(String methodName)
		{
			this.methodName = methodName;
			return this;
		}
		
		/**
		 * @param		classPath Method class file path
		 * @return		Builder to allow chained calls
		 */
		public ClassMethodInfoBuilder classPath(Path classPath)
		{
			this.classPath = classPath;
			return this;
		}
		
		/**
		 * @param		testClassPath Test method class file path
		 * @return		Builder to allow chained calls
		 */
		public ClassMethodInfoBuilder testClassPath(Path testClassPath)
		{
			this.testClassPath = testClassPath;
			return this;
		}
		
		/**
		 * @param		srcPath Path where source file is
		 * @return		Builder to allow chained calls
		 */
		public ClassMethodInfoBuilder srcPath(Path srcPath)
		{
			this.srcPath = srcPath;
			return this;
		}
		
		/**
		 * @param		testSrcPath Path where source file of test method is
		 * @return		Builder to allow chained calls
		 */
		public ClassMethodInfoBuilder testSrcPath(Path testSrcPath)
		{
			this.testSrcPath = testSrcPath;
			return this;
		}
		
		/**
		 * @param		classSignature Signature of the class
		 * @return		Builder to allow chained calls
		 */
		public ClassMethodInfoBuilder classSignature(String classSignature)
		{
			this.classSignature = classSignature;
			return this;
		}
		
		/**
		 * @param		methodSignature Signature of the method
		 * @return		Builder to allow chained calls
		 */
		public ClassMethodInfoBuilder methodSignature(String methodSignature)
		{
			this.methodSignature = methodSignature;
			return this;
		}
		
		/**
		 * @param		testMethodSignature Signature of the test method to 
		 * which the method belongs
		 * @return		Builder to allow chained calls
		 */
		public ClassMethodInfoBuilder testMethodSignature(String testMethodSignature)
		{
			this.testMethodSignature = testMethodSignature;
			return this;
		}
		
		/**
		 * @param		invocationLine Line of test method where method is 
		 * called
		 * @return		Builder to allow chained calls
		 */
		public ClassMethodInfoBuilder invocationLine(int invocationLine)
		{
			this.invocationLine = invocationLine;
			return this;
		}
		
		/**
		 * @param		parameterTypes Types of method's parameters
		 * @return		Builder to allow chained calls
		 */
		public ClassMethodInfoBuilder parameterTypes(Class<?>[] parameterTypes)
		{
			this.parameterTypes = parameterTypes;
			return this;
		}
		
		/**
		 * @param		args Method's arguments
		 * @return		Builder to allow chained calls
		 */
		public ClassMethodInfoBuilder args(Object... args)
		{
			this.args = args;
			return this;
		}
		
		/**
		 * @param		returnType Return type of the method
		 * @return		Builder to allow chained calls
		 */
		public ClassMethodInfoBuilder returnType(Class<?> returnType)
		{
			this.returnType = returnType;
			return this;
		}
		
		/**
		 * Creates {@link ClassMethodInfo} with provided information. It is
		 * necessary that required fields must be filled. The required 
		 * fields are: <br />
		 * <ul>
		 * 		<li>classPath</li>
		 * 		<li>testClassPath</li>
		 * 		<li>srcPath</li>
		 * 		<li>testSrcPath</li>
		 * 		<li>invocationLine</li>
		 * 		<li>methodSignature</li>
		 * 		<li>classSignature</li>
		 * 		<li>testMethodSignature</li>
		 * 		<li>methodName</li>
		 * </ul>
		 * 
		 * @return		ClassMethodInfo with provided information
		 * @throws		IllegalArgumentException If any required field is null
		 */
		public ClassMethodInfo build() throws IllegalArgumentException
		{
			String nullField = null;
			
			if (classPath == null)
				nullField = "classPath";
			else if (testClassPath == null)
				nullField = "testClassPath";
			else if (srcPath == null)
				nullField = "srcPath";
			else if (testSrcPath == null)
				nullField = "testSrcPath";
			else if (invocationLine <= 0)
				nullField = "invocationLine";
			else if (classSignature == null)
				nullField = "classSignature";
			else if (methodSignature == null)
				nullField = "methodSignature";
			else if (testMethodSignature == null)
				nullField = "testMethodSignature";
			else if (methodName == null)
				nullField = "methodName";
			
			if (nullField != null)
				throw new IllegalArgumentException("Required fields cannot be null: "+nullField);
			
			return new ClassMethodInfo(
				classPath, testClassPath, srcPath, testSrcPath, invocationLine, 
				classSignature, methodSignature, testMethodSignature, methodName, 
				returnType, parameterTypes, args
			);
		}
	}
	
	
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	@Override
	public String toString() 
	{
		return "ClassMethodInfo [methodName=" + methodName + ", classPath=" 
				+ classPath + ", testClassPath=" + testClassPath + ", srcPath=" 
				+ srcPath + ", testMethodSrcPath=" + testSrcPath 
				+ ", testMethodSignature=" + testMethodSignature 
				+ ", classSignature=" + classSignature 
				+ ", methodSignature=" + methodSignature 
				+ ", invocationLine=" + invocationLine 
				+ ", parameterTypes=" + Arrays.toString(parameterTypes) 
				+ ", args="	+ Arrays.toString(args) 
				+ ", returnType=" + returnType + "]";
	}

	/**
	 * Extracts test method signature and method signature.
	 * 
	 * @return		Test method signature and method signature
	 */
	public SignaturesInfo extractSignatures()
	{
		return new SignaturesInfo(methodSignature, testMethodSignature);
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
	 * Gets path of the compiled file of the test method.
	 * 
	 * @return		Path of the compiled file of the test method
	 */
	public Path getTestClassPath()
	{
		return this.testClassPath;
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
	 * Gets the path of the test method source file.
	 * 
	 * @return		Path of the test method source file
	 */
	public Path getTestSrcPath()
	{
		return this.testSrcPath;
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
	 * Gets test method signature.
	 * 
	 * @return		Test method signature
	 */
	public String getTestMethodSignature() 
	{
		return testMethodSignature;
	}
	
	/**
	 * Gets class signature. 
	 * 
	 * @return		Class signature
	 */
	public String getClassSignature()
	{
		return this.classSignature;
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
	 * @return		Package to which the method belongs
	 */
	public String getPackage()
	{
		if (classSignature == null) { return ""; }
		
		return extractPackage(classSignature);
	}
	
	/**
	 * Gets package of the test method.
	 * 
	 * @return		Package to which the method belongs
	 */
	public String getTestClassPackage()
	{
		return extractPackage(getTestClassSignature());
	}
	
	/**
	 * Gets class signature from the test method. 
	 * 
	 * @return		Class signature
	 */
	public String getTestClassSignature()
	{
		if (testMethodSignature == null) { return ""; }
		
		return extractClassSignature(testMethodSignature);
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
	 * Gets directory where the test method's compiled file is.
	 * 
	 * @return		Directory where the compiled test method file is
	 */
	public Path getTestClassDirectory()
	{
		return getCompiledFileDirectory(testClassPath);
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