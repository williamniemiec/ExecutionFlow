package executionFlow.info;

import static java.lang.invoke.MethodType.methodType;

import java.lang.invoke.MethodType;
import java.util.Arrays;


/**
 * Stores information about a method.
 */
public class ClassMethodInfo 
{
	//-----------------------------------------------------------------------
	//		Attributes
	//-----------------------------------------------------------------------
	private String methodName;
	private Class<?>[] parameterTypes;
	private Object[] args;
	private Class<?> returnType;
	private String classPath;
	private String testMethodSignature;
	private String methodSignature;
	private int invocationLine;
	private String srcPath;
	
	
	//-----------------------------------------------------------------------
	//		Constructors
	//-----------------------------------------------------------------------
	/**
	 * Stores information about a method.
	 * 
	 * @param classPath Method class file path
	 * @param srcPath
	 * @param methodSignature Signature of the method
	 * @param testMethodSignature Signature of the test method to which the method belongs
	 * @param methodName Method's name
	 * @param returnType Return type of the method
	 * @param parameterTypes Types of method's parameters
	 * @param args Method's arguments
	 */
	private ClassMethodInfo(String classPath, String srcPath, int invocationLine, String methodSignature, 
			String testMethodSignature, String methodName, Class<?> returnType, Class<?>[] parameterTypes, Object... args) 
	{
		this.classPath = classPath;
		this.srcPath = srcPath;
		this.methodSignature = methodSignature;
		this.testMethodSignature = testMethodSignature;
		this.methodName = methodName;
		this.returnType = returnType;
		this.parameterTypes = parameterTypes;
		this.args = args;
		this.invocationLine = invocationLine;
	}

	/**
	 * Builder of this class.
	 */
	public static class ClassMethodInfoBuilder
	{
		private String classPath;
		private String methodSignature;
		private String testMethodSignature;
		private String methodName;
		private Class<?>[] parameterTypes;
		private Object[] args;
		private Class<?> returnType = void.class;
		private int invocationLine;
		private String srcPath;
		
		public ClassMethodInfoBuilder invocationLine(int invocationLine)
		{
			this.invocationLine = invocationLine;
			return this;
		}
		
		public ClassMethodInfoBuilder methodName(String methodName)
		{
			this.methodName = methodName;
			return this;
		}
		
		public ClassMethodInfoBuilder methodSignature(String methodSignature)
		{
			this.methodSignature = methodSignature;
			return this;
		}
		
		public ClassMethodInfoBuilder parameterTypes(Class<?>[] parameterTypes)
		{
			this.parameterTypes = parameterTypes;
			return this;
		}
		
		public ClassMethodInfoBuilder args(Object... args)
		{
			this.args = args;
			return this;
		}

		public ClassMethodInfoBuilder classPath(String classPath)
		{
			this.classPath = classPath;
			return this;
		}
		
		public ClassMethodInfoBuilder testMethodSignature(String testMethodSignature)
		{
			this.testMethodSignature = testMethodSignature;
			return this;
		}
		
		public ClassMethodInfoBuilder returnType(Class<?> returnType)
		{
			this.returnType = returnType;
			return this;
		}
		
		public ClassMethodInfoBuilder srcPath(String srcPath)
		{
			this.srcPath = srcPath;
			return this;
		}
		
		public ClassMethodInfo build()
		{
			return new ClassMethodInfo(
				classPath, srcPath, invocationLine, methodSignature, testMethodSignature, methodName, 
				returnType, parameterTypes, args
			);
		}
	}
	
	
	//-----------------------------------------------------------------------
	//		Methods
	//-----------------------------------------------------------------------
	@Override
	public String toString() 
	{
		return "ClassMethodInfo [methodName=" + methodName + ", invocationLine=" + invocationLine 
				+ ", parameterTypes=" + Arrays.toString(parameterTypes)
				+ ", args=" + Arrays.toString(args) + ", classPath=" + classPath + ", srcPath=" + srcPath 
				+ ", testMethodSignature=" + testMethodSignature + ", returnType=" + returnType
				+ ", methodSignature=" + methodSignature + "]";
	}

	/**
	 * Extracts test method's signature and method's signature.
	 * 
	 * @return {@link SignaturesInfo} with the signatures
	 */
	public SignaturesInfo extractSignatures()
	{
		String paramsTypes = extractParameterTypes(parameterTypes);
		String methodSig = methodSignature+"."+methodName+"("+paramsTypes+")";
		
		return new SignaturesInfo(methodSig, testMethodSignature);
	}
	
	/**
	 * Extracts the types of the method parameters.
	 * 
	 * @param parametersTypes Types of each method's parameter
	 * @return String with the name of each type separated by commas
	 */
	private String extractParameterTypes(Class<?>[] parametersTypes)
	{
		if (parametersTypes == null) { return ""; }
		StringBuilder parameterTypes = new StringBuilder();
		
		for (var parameterType : parametersTypes) {
			// Ads only name of the parameter type
			String[] tmp = parameterType.getTypeName().split("\\."); // Removes signature
			
			parameterTypes.append(tmp[tmp.length-1] +",");
		}
		
		if (parameterTypes.length() > 0)
			parameterTypes.deleteCharAt(parameterTypes.length()-1);	// Removes last comma
		
		return parameterTypes.toString();
	}
	
	
	//-----------------------------------------------------------------------
	//		Getters & Setters
	//-----------------------------------------------------------------------
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
	
	/**
	 * Get method's signature with the following format:<br />
	 * <code> methodPackage.methodName(arg1,arg2,...) </code>
	 * 
	 * @return Method's signature
	 */
	public String getSignature()
	{
		if (parameterTypes == null) { return methodName+"()"; }
		
		StringBuilder types = new StringBuilder();
		
		for (Class<?> paramType : parameterTypes) {
			types.append(paramType.getTypeName()+",");
		}
		
		if (types.length() > 0)
			types.deleteCharAt(types.length()-1);	// Remove last comma
		
		return methodName+"("+types+")";
	}
	
	public String getTestMethodSignature() 
	{
		return testMethodSignature;
	}

	public void setTestMethodSignature(String testMethodSignature) 
	{
		this.testMethodSignature = testMethodSignature;
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
	
	public String getPackage()
	{
		if (methodSignature == null) { return ""; }
		
		String[] tmp = methodSignature.split("\\.");
		StringBuilder sb = new StringBuilder();
		
		for (int i=0; i<tmp.length-1; i++) {
			sb.append(tmp[i]);
			sb.append(".");
		}
		
		if (sb.length() > 0) {
			sb.deleteCharAt(sb.length()-1);
		}
		
		return sb.toString();
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