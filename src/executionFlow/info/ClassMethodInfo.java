package executionFlow.info;

import java.util.Arrays;


/**
 * Stores information about a class' method.
 */
public class ClassMethodInfo 
{
	//-----------------------------------------------------------------------
	//		Attributes
	//-----------------------------------------------------------------------
	private String methodName;
	private Class<?>[] parameterTypes;
	private Object[] args;
	private String classPath;
	private String testMethodSignature;
	private ClassConstructorInfo constructor;
	private Class<?> returnType;
	private String methodSignature;
	
	
	//-----------------------------------------------------------------------
	//		Constructors
	//-----------------------------------------------------------------------
	private ClassMethodInfo(String classPath, String methodSignature, String testMethodSignature, String methodName, Class<?> returnType, Class<?>[] parameterTypes, Object... args) 
	{
		this(testMethodSignature, methodName, parameterTypes, args);
		this.classPath = classPath;
		this.returnType = returnType;
		this.methodSignature = methodSignature;
	}
	
	
	
//	public ClassMethodInfo(String classPath, String methodSignature, String testMethodSignature, String methodName, Class<?> returnType, Class<?>[] parameterTypes, Object... args) 
//	{
//		this(testMethodSignature, methodName, parameterTypes, args);
//		this.classPath = classPath;
//		this.returnType = returnType;
//		this.methodSignature = methodSignature;
//	}
	
	
	
	/**
	 * Create a MethodInfo for a method with arguments and with the signature of the
	 * test method to which it belongs.
	 * 
	 * @param testMethodSignature Signature of the test method to which the method belongs
	 * @param methodName Method's name
	 * @param parameterTypes Types of method's parameters
	 * @param args Method's values
	 */
	public ClassMethodInfo(String testMethodSignature, String methodName, Class<?>[] parameterTypes, Object... args) 
	{
		this(methodName, parameterTypes, args);
		this.testMethodSignature = testMethodSignature;
	}
	
	/**
	 * Create a MethodInfo for a method with arguments.
	 * 
	 * @param methodName Method's name
	 * @param parameterTypes Types of method's parameters
	 * @param args Method's values
	 */
	public ClassMethodInfo(String methodName, Class<?>[] parameterTypes, Object... args) 
	{
		this.methodName = methodName;
		this.parameterTypes = parameterTypes;
		this.args = args;
	}
	
	/**
	 * Create a MethodInfo for a method without arguments.
	 * 
	 * @param methodName Method's name
	 */
	public ClassMethodInfo(String methodName) 
	{
		this.methodName = methodName;
	}

	
	public static class ClassMethodInfoBuilder
	{
		private String classPath;
		private String methodSignature;
		private String testMethodSignature;
		private String methodName;
		private Class<?>[] parameterTypes;
		private Object[] args;
		private Class<?> returnType;
		
		
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
		
		public ClassMethodInfoBuilder args(Object[] args)
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
			return new ClassMethodInfo(classPath, methodSignature, testMethodSignature, methodName, returnType, parameterTypes, args);
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
				+ testMethodSignature + ", constructor=" + constructor + ", returnType=" + returnType
				+ ", methodSignature=" + methodSignature + "]";
	}

	/**
	 * Extracts test method's signature and method's signature.
	 * 
	 * @return {@link SignaturesInfo} with the signatures
	 */
	public SignaturesInfo extractSignatures()
	{
//		Method m = classExecutionFlow.getMethod(cmi.getSignature());
		String paramsTypes = extractParameterTypes(parameterTypes);
//		System.out.println();
//		System.out.println(cmi.getSignature());
//		System.out.println();
		
		//String methodSignature = classExecutionFlow.getClassSignature()+"."+m.getName()+"("+parameterTypes+")";
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
	
	public void setConstructor(ClassConstructorInfo constructor)
	{
		this.constructor = constructor;
	}
	
	public ClassConstructorInfo getClassConstructorInfo()
	{
		return this.constructor;
	}
	
	public Class<?> getReturnType()
	{
		return this.returnType;
	}
	
	public String getMethodSignature()
	{
		return this.methodSignature;
	}
}