package executionFlow.info;


/**
 * Stores information about a method and its constructor (if this 
 * method is not static);
 * 
 * @author	William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version	1.4
 * @since	1.0
 */
public class CollectorInfo 
{
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	/**
	 * Stores {@link ClassMethodInfo information} about a method.
	 */
	private ClassMethodInfo methodInfo;
	
	/**
	 * Stores {@link ClassConstructorInfo information} about the constructor of
	 * {@link #methodInfo the method}.
	 */
	private ClassConstructorInfo constructorInfo;
	
	/**
	 * The order of a method is how many methods are called in this line before
	 * it.
	 */
	private int order;
	
	
	//-------------------------------------------------------------------------
	//		Constructor
	//-------------------------------------------------------------------------
	/**
	 * Stores information about a method, with its order and constructor.
	 * 
	 * @param methodInfo Information about a method
	 * @param constructorInfo Information about the constructor of the method
	 * @param order Order in which the method is called. It is used when more
	 * than one method is called in a single line. The order of a method is
	 * how many methods are called in this line before it
	 */
	public CollectorInfo(ClassMethodInfo methodInfo, ClassConstructorInfo constructorInfo, int order)
	{
		this.methodInfo = methodInfo;
		this.constructorInfo = constructorInfo;
		this.order = order;
	}
	
	/**
	 * Stores information about a method, with its order and constructor. Using
	 * this constructor, {@link #constructorInfo method constructor} will be 
	 * null and the {@link order} of the method will be zero.
	 * 
	 * @param methodInfo Information about a method
	 * @param constructorInfo Information about the constructor of the method
	 * @param order Order in which the method is called. It is used when more
	 * than one method is called in a single line. The order of a method is
	 * how many methods are called in this line before it
	 */
	public CollectorInfo(ClassMethodInfo methodInfo)
	{
		this(methodInfo, 0);
	}
	
	/**
	 * Stores information about a method, with its order and constructor. Using
	 * this constructor the {@link order} of method will be zero.
	 * 
	 * @param methodInfo Information about a method
	 * @param constructorInfo Information about the constructor of the method
	 */
	public CollectorInfo(ClassMethodInfo methodInfo, ClassConstructorInfo constructorInfo)
	{
		this(methodInfo, constructorInfo, 0);
	}
	
	/**
	 * Stores information about a method with its order. Use this method when
	 * you do not want to save information about constructor.
	 * 
	 * @param methodInfo Information about a method
	 * @param order Order in which the method is called. It is used when more
	 * than one method is called in a single line. The order of a method is
	 * how many methods are called in this line before it
	 */
	public CollectorInfo(ClassMethodInfo methodInfo, int order)
	{
		this(methodInfo, null, order);
	}


	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	@Override
	public String toString() {
		return "CollectorInfo [order=" + order + ", constructorInfo=" + 
				constructorInfo + ", methodInfo=" + methodInfo + "]";
	}

	
	//-------------------------------------------------------------------------
	//		Getters & Setters
	//-------------------------------------------------------------------------
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
