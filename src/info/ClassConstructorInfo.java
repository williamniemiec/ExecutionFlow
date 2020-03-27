package info;


public class ClassConstructorInfo 
{
	//-----------------------------------------------------------------------
	//		Attributes
	//-----------------------------------------------------------------------
	private Class<?>[] initTypes;
	private Object[] initArgs;
	
	
	//-----------------------------------------------------------------------
	//		Constructor
	//-----------------------------------------------------------------------
	public ClassConstructorInfo(Class<?>[] initTypes, Object... initArgs) 
	{
		this.initTypes = initTypes;
		this.initArgs = initArgs;
	}
	

	//-----------------------------------------------------------------------
	//		Getters
	//-----------------------------------------------------------------------
	public Class<?>[] getInitTypes() 
	{
		return initTypes;
	}
	
	public Object[] getInitArgs() 
	{
		return initArgs;
	}
}
