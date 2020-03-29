package info;

import java.util.Arrays;


public class ClassMethodInfo 
{
	//-----------------------------------------------------------------------
	//		Attributes
	//-----------------------------------------------------------------------
	private String methodName;
	private Class<?>[] parameterTypes;
	private Object[] args;
	private Object instance;
	
	
	//-----------------------------------------------------------------------
	//		Constructors
	//-----------------------------------------------------------------------
	public ClassMethodInfo(String methodName, Class<?>[] parameterTypes, Object... args) 
	{
		this.methodName = methodName;
		this.parameterTypes = parameterTypes;
		this.args = args;
	}
	
	public ClassMethodInfo(String methodName) 
	{
		this.methodName = methodName;
	}
	
	
	public ClassMethodInfo(Object instance, String methodName, Class<?>[] parameterTypes, Object... args) 
	{
		this(methodName, parameterTypes, args);
		this.instance = instance;
	}

	
	//-----------------------------------------------------------------------
	//		Getters & Setters
	//-----------------------------------------------------------------------
	public String getMethodName() 
	{
		return methodName;
	}
	
	public String getSignature()
	{
		StringBuilder types = new StringBuilder();
		
		if (parameterTypes != null) {
			for (Class<?> paramType : parameterTypes) {
				types.append(paramType.getTypeName()+",");
			}
			
			if (types.length() > 0)
				types.deleteCharAt(types.length()-1);	// Remove last comma
		}
		
		return methodName+"("+types+")";
	}

	public Class<?>[] getParameterTypes() 
	{
		return parameterTypes;
	}

	public Object[] getArgs() 
	{
		return args;
	}
	
	public Object getInstance() 
	{
		return instance;
	}
	
	public void setInstance(Object instance) 
	{
		this.instance = instance;
	}
	
	
	//-----------------------------------------------------------------------
	//		Methods
	//-----------------------------------------------------------------------
	public boolean hasInstance()
	{
		return instance != null;
	}

	@Override
	public String toString() 
	{
		return "ClassMethodInfo [methodName=" + methodName
				+ ", parameterTypes=" + Arrays.toString(parameterTypes)
				+ ", args=" + Arrays.toString(args) + ", instance=" + instance
				+ "]";
	}
}