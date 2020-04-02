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
	
	
	//-----------------------------------------------------------------------
	//		Constructors
	//-----------------------------------------------------------------------
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

	
	//-----------------------------------------------------------------------
	//		Methods
	//-----------------------------------------------------------------------
	@Override
	public String toString() 
	{
		return "ClassMethodInfo [methodName=" + methodName + ", parameterTypes=" + Arrays.toString(parameterTypes)
		+ ", args=" + Arrays.toString(args) + ", classPath=" + classPath + "]";
	}	
}