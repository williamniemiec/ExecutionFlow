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
	
	
	//-----------------------------------------------------------------------
	//		Constructors
	//-----------------------------------------------------------------------
	/**
	 * Stores information about a method.
	 * 
	 * @param classPath Method class file path
	 * @param methodSignature Signature of the method
	 * @param testMethodSignature Signature of the test method to which the method belongs
	 * @param methodName Method's name
	 * @param returnType Return type of the method
	 * @param parameterTypes Types of method's parameters
	 * @param args Method's arguments
	 */
	private ClassMethodInfo(String classPath, String methodSignature, String testMethodSignature, 
							String methodName, Class<?> returnType, Class<?>[] parameterTypes, Object... args) 
	{
		this.classPath = classPath;
		this.methodSignature = methodSignature;
		this.testMethodSignature = testMethodSignature;
		this.methodName = methodName;
		this.returnType = returnType;
		this.parameterTypes = parameterTypes;
		this.args = args;
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
		
		public ClassMethodInfo build()
		{
			return new ClassMethodInfo(	classPath, methodSignature, testMethodSignature, methodName, 
										returnType, parameterTypes, args);
		}
	}
	
	
	//-----------------------------------------------------------------------
	//		Methods
	//-----------------------------------------------------------------------
	@Override
	public String toString() 
	{
		return "ClassMethodInfo [methodName=" + methodName + ", parameterTypes=" + Arrays.toString(parameterTypes)
				+ ", args=" + Arrays.toString(args) + ", classPath=" + classPath + ", testMethodSignature="
				+ testMethodSignature + ", returnType=" + returnType
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
}