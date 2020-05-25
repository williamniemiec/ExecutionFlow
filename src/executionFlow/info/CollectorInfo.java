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
	private int order;
	
	
	//-----------------------------------------------------------------------
	//		Constructor
	//-----------------------------------------------------------------------
	public CollectorInfo(ClassMethodInfo methodInfo)
	{
		this(methodInfo, 0);
	}
	
	public CollectorInfo(ClassMethodInfo methodInfo, ClassConstructorInfo constructorInfo)
	{
		this(methodInfo, 0);
		this.constructorInfo = constructorInfo;
	}
	
	public CollectorInfo(ClassMethodInfo methodInfo, int order)
	{
		this.methodInfo = methodInfo;
		this.order = order;
	}


	//-----------------------------------------------------------------------
	//		Methods
	//-----------------------------------------------------------------------
	@Override
	public String toString() {
		return "CollectorInfo [order=" + order + ", constructorInfo=" + 
				constructorInfo + ", methodInfo=" + methodInfo + "]";
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
	
	public int getOrder()
	{
		return this.order;
	}
}
