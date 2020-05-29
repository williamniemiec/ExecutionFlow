package executionFlow.info;

import static java.lang.invoke.MethodType.methodType;

import java.lang.invoke.MethodType;
import java.util.Arrays;


/**
 * Stores information about a method.
 * 
 * @author William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @since 1.0
 * @version 1.4
 */
public class ClassMethodInfo 
{
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	private String methodName;
	private String classPath;
	private String testClassPath;
	private String srcPath;
	private String testSrcPath;
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
	 * @param classPath Method class file path
	 * @param testClassPath Test method class file path
	 * @param srcPath Absolute path where source file is
	 * @param testSrcPath Absolute path where source file of test method is
	 * @param invocationLine Line of test method where method is called
	 * @param methodSignature Signature of the method
	 * @param testMethodSignature Signature of the test method to which the 
	 * method belongs
	 * @param methodName Method's name
	 * @param returnType Return type of the method
	 * @param parameterTypes Types of method's parameters
	 * @param args Method's arguments
	 */
	private ClassMethodInfo(String classPath, String testClassPath, 
			String srcPath, String testSrcPath, int invocationLine, 
			String methodSignature, String testMethodSignature, 
			String methodName, Class<?> returnType, Class<?>[] parameterTypes, 
			Object... args) 
	{
		this.classPath = classPath;
		this.testClassPath = testClassPath;
		this.srcPath = srcPath;
		this.testSrcPath = testSrcPath;
		this.methodSignature = methodSignature;
		this.testMethodSignature = testMethodSignature;
		this.methodName = methodName;
		this.returnType = returnType;
		this.parameterTypes = parameterTypes;
		this.args = args;
		this.invocationLine = invocationLine;
		this.classSignature = extractClassSignature(methodSignature);
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
	 * 		<li>testMethodSignature</li>
	 * 		<li>methodName</li>
	 * </ul>
	 */
	public static class ClassMethodInfoBuilder
	{
		private String methodName;
		private String classPath;
		private String testClassPath;
		private String srcPath;
		private String testSrcPath;
		private String methodSignature;
		private String testMethodSignature;
		private int invocationLine;
		private Class<?>[] parameterTypes;
		private Object[] args;
		private Class<?> returnType = void.class;
		
		
		/**
		 * @param methodName Method's name
		 * @return Builder to allow chained calls
		 */
		public ClassMethodInfoBuilder methodName(String methodName)
		{
			this.methodName = methodName;
			return this;
		}
		
		/**
		 * @param classPath Method class file path
		 * @return Builder to allow chained calls
		 */
		public ClassMethodInfoBuilder classPath(String classPath)
		{
			this.classPath = classPath;
			return this;
		}
		
		/**
		 * @param testClassPath Test method class file path
		 * @return Builder to allow chained calls
		 */
		public ClassMethodInfoBuilder testClassPath(String testClassPath)
		{
			this.testClassPath = testClassPath;
			return this;
		}
		
		/**
		 * @param srcPath Absolute path where source file is
		 * @return Builder to allow chained calls
		 */
		public ClassMethodInfoBuilder srcPath(String srcPath)
		{
			this.srcPath = srcPath;
			return this;
		}
		
		/**
		 * @param testSrcPath Absolute path where source file of test method is
		 * @return Builder to allow chained calls
		 */
		public ClassMethodInfoBuilder testSrcPath(String testSrcPath)
		{
			this.testSrcPath = testSrcPath;
			return this;
		}
		
		/**
		 * @param methodSignature Signature of the method
		 * @return Builder to allow chained calls
		 */
		public ClassMethodInfoBuilder methodSignature(String methodSignature)
		{
			this.methodSignature = methodSignature;
			return this;
		}
		
		/**
		 * @param testMethodSignature Signature of the test method to which 
		 * the method belongs
		 * @return Builder to allow chained calls
		 */
		public ClassMethodInfoBuilder testMethodSignature(String testMethodSignature)
		{
			this.testMethodSignature = testMethodSignature;
			return this;
		}
		
		/**
		 * @param invocationLine Line of test method where method is called
		 * @return Builder to allow chained calls
		 */
		public ClassMethodInfoBuilder invocationLine(int invocationLine)
		{
			this.invocationLine = invocationLine;
			return this;
		}
		
		/**
		 * @param parameterTypes Types of method's parameters
		 * @return Builder to allow chained calls
		 */
		public ClassMethodInfoBuilder parameterTypes(Class<?>[] parameterTypes)
		{
			this.parameterTypes = parameterTypes;
			return this;
		}
		
		/**
		 * @param args Method's arguments
		 * @return Builder to allow chained calls
		 */
		public ClassMethodInfoBuilder args(Object... args)
		{
			this.args = args;
			return this;
		}
		
		/**
		 * @param returnType Return type of the method
		 * @return Builder to allow chained calls
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
		 * 		<li>testMethodSignature</li>
		 * 		<li>methodName</li>
		 * </ul>
		 * 
		 * @return ClassMethodInfo with provided information
		 * @throws IllegalArgumentException If any required field is null
		 */
		public ClassMethodInfo build() throws IllegalArgumentException
		{
			if (isAnyRequiredFieldNull()) {
				throw new IllegalArgumentException("Required fields cannot be null");
			}
			
			return new ClassMethodInfo(
				classPath, testClassPath, srcPath, testSrcPath, invocationLine, 
				methodSignature, testMethodSignature, methodName, returnType, 
				parameterTypes, args
			);
		}
		
		/**
		 * Checks if there is a required field that is null. The required 
		 * fields are: <br />
		 * <ul>
		 * 		<li>classPath</li>
		 * 		<li>testClassPath</li>
		 * 		<li>srcPath</li>
		 * 		<li>testSrcPath</li>
		 * 		<li>invocationLine</li>
		 * 		<li>methodSignature</li>
		 * 		<li>testMethodSignature</li>
		 * 		<li>methodName</li>
		 * </ul>
		 * 
		 * @return If there is a required field that is null
		 */
		private boolean isAnyRequiredFieldNull()
		{
			return 	classPath == null || 
					classPath == null || 
					testClassPath == null || 
					srcPath == null || 
					testSrcPath == null || 
					invocationLine <= 0 || 
					methodSignature == null ||
					testMethodSignature == null ||
					methodName == null;
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
				+ ", methodSignature=" + methodSignature + ", classSignature=" 
				+ classSignature + ", invocationLine=" + invocationLine 
				+ ", parameterTypes=" + Arrays.toString(parameterTypes) 
				+ ", args="	+ Arrays.toString(args) 
				+ ", returnType=" + returnType + "]";
	}

	/**
	 * Extracts test method's signature and method's signature.
	 * 
	 * @return {@link SignaturesInfo} with the signatures
	 */
	public SignaturesInfo extractSignatures()
	{
		return new SignaturesInfo(methodSignature, testMethodSignature);
	}
	
	/**
	 * Extracts the types of the method parameters.
	 * 
	 * @param parametersTypes Types of each method's parameter
	 * @return String with the name of each type separated by commas
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
	 * Extracts class signature from method signature.
	 * 
	 * @param methodSignature Signature of the method
	 * @return Class signature
	 */
	private String extractClassSignature(String methodSignature)
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
	 * @param signature Signature of the class
	 * @return Class package
	 */
	private String extractPackage(String signature)
	{
		if (signature == null || signature.isEmpty()) { return ""; }
		
		String[] tmp = signature.split("\\.");
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
	
	
	//-------------------------------------------------------------------------
	//		Getters & Setters
	//-------------------------------------------------------------------------
	public String getMethodName() 
	{
		return methodName;
	}

	public Class<?>[] getParameterTypes() 
	{
		return parameterTypes;
	}
	
	public Object[] getArgs() 
	{
		return args;
	}
	
	public String getClassPath()
	{
		return this.classPath;
	}
	
	public void setClassPath(String classPath)
	{
		this.classPath = classPath;
	}
	
	public String getTestClassPath()
	{
		return this.testClassPath;
	}
	
	public String getTestMethodSignature() 
	{
		return testMethodSignature;
	}
	
	public Class<?> getReturnType()
	{
		return this.returnType;
	}
	
	public String getMethodSignature()
	{
		return this.methodSignature;
	}
	
	public int getInvocationLine()
	{
		return this.invocationLine;
	}
	
	public String getSrcPath()
	{
		return this.srcPath;
	}
	
	public String getTestSrcPath()
	{
		return this.testSrcPath;
	}
	
	public String getClassSignature()
	{
		return this.classSignature;
	}
	
	public String getPackage()
	{
		if (classSignature == null) { return ""; }
		
		return extractPackage(classSignature);
	}
	
	public String getTestClassPackage()
	{
		return extractPackage(getTestClassSignature());
	}
	
	public String getTestClassSignature()
	{
		if (testMethodSignature == null) { return ""; }
		
		return extractClassSignature(testMethodSignature);
	}
	
	/**
	 * Gets parameter types and return type of the method.
	 * 
	 * @return Return type and parameter types of the method
	 */
	public MethodType getMethodTypes() 
	{
		if (args == null || args.length == 0)
			return methodType(returnType);
		
		return methodType(returnType, parameterTypes);
	}
	
	public String getTestClassDirectory()
	{
		StringBuilder response = new StringBuilder();
		String[] terms = testClassPath.split("\\\\");
		
		for (int i=0; i<terms.length-1; i++) {
			response.append(terms[i]);
			response.append("\\");
		}
		
		if (response.length() > 0) {
			response.deleteCharAt(response.length()-1);
		}
		
		return response.toString();
	}
	
	public String getClassDirectory()
	{
		StringBuilder response = new StringBuilder();
		String[] terms = classPath.split("\\\\");
		
		for (int i=0; i<terms.length-1; i++) {
			response.append(terms[i]);
			response.append("\\");
		}
		
		if (response.length() > 0) {
			response.deleteCharAt(response.length()-1);
		}
		
		return response.toString();
	}
	
	public String getSrcDirectory()
	{
		StringBuilder response = new StringBuilder();
		String[] terms = srcPath.split("\\\\");
		
		for (int i=0; i<terms.length-1; i++) {
			response.append(terms[i]);
			response.append("\\");
		}
		
		if (response.length() > 0) {
			response.deleteCharAt(response.length()-1);
		}
		
		return response.toString();
	}
}