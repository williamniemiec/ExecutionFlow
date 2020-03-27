package info;


public class ClassMethodInfo 
{
	//-----------------------------------------------------------------------
	//		Attributes
	//-----------------------------------------------------------------------
	private String methodName;
	private Class<?>[] parameterTypes;
	private Object[] args;
	
	
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

	
	//-----------------------------------------------------------------------
	//		Getters
	//-----------------------------------------------------------------------
	public String getMethodName() 
	{
		return methodName;
	}
	
	public String getSignature()
	{
		StringBuilder types = new StringBuilder();
		
		if (parameterTypes != null) {
			for (var paramType : parameterTypes) {
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
}
