package examples.overloadedMethod;


/**
 * Class that has an overloaded method.
 */
public class OverloadClass 
{
	//-----------------------------------------------------------------------
	//		Attributes
	//-----------------------------------------------------------------------
	private int num;
	
	
	//-----------------------------------------------------------------------
	//		Constructors
	//-----------------------------------------------------------------------
	public OverloadClass(int num)
	{
		this.num = num;
	}
	
	public OverloadClass()
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
		int x = 0;
		x++;
		x++;
		return x+overloadedMethod(10);
	}
}