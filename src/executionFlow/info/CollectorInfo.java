package executionFlow.info;


/**
 * Stores information about a method and its constructor (if this 
 * method is not static);
 */
public class CollectorInfo 
{
	//-----------------------------------------------------------------------
	//		Attributes
	//-----------------------------------------------------------------------
	private ClassConstructorInfo constructorInfo;
	private ClassMethodInfo methodInfo;
	
	
	//-----------------------------------------------------------------------
	//		Constructor
	//-----------------------------------------------------------------------
	public CollectorInfo(ClassMethodInfo methodInfo)
	{
		this.methodInfo = methodInfo;
	}
	
	public CollectorInfo(ClassMethodInfo methodInfo, ClassConstructorInfo constructorInfo)
	{
		this(methodInfo);
		this.constructorInfo = constructorInfo;
	}


	//-----------------------------------------------------------------------
	//		Methods
	//-----------------------------------------------------------------------
	@Override
	public String toString() {
		return "CollectorInfo [constructorInfo=" + constructorInfo + ", methodInfo=" + methodInfo + "]";
	}

	
	//-----------------------------------------------------------------------
	//		Getters & Setters
	//-----------------------------------------------------------------------
	public ClassConstructorInfo getConstructorInfo()
	{
		return constructorInfo;
	}

	public void setConstructorInfo(ClassConstructorInfo constructorInfo) 
	{
		this.constructorInfo = constructorInfo;
	}

	public ClassMethodInfo getMethodInfo() 
	{
		return methodInfo;
	}

	public void setMethodInfo(ClassMethodInfo methodInfo) 
	{
		this.methodInfo = methodInfo;
	}
}
