package overloadedMethod;


/**
 * Class that has an overloaded method.
 */
public class OverloadedConstructor 
{
	//-----------------------------------------------------------------------
	//		Attributes
	//-----------------------------------------------------------------------
	private int num;
	
	
	//-----------------------------------------------------------------------
	//		Constructors
	//-----------------------------------------------------------------------
	public OverloadedConstructor(int num)
	{
		this.num = num;
	}
	
	public OverloadedConstructor()
	{
		this(0);
	}
	
	
	//-----------------------------------------------------------------------
	//		Methods
	//-----------------------------------------------------------------------
	public int overloadedMethod(int num)
	{
		num *= 10;
		return num;
	}
	
	public int overloadedMethod()
	{
		return overloadedMethod(10);
	}
}
