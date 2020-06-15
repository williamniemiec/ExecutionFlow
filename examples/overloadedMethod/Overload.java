package overloadedMethod;


/**
 * Class that has an overloaded method.
 */
public class Overload 
{
	//-----------------------------------------------------------------------
	//		Attributes
	//-----------------------------------------------------------------------
	private int num;
	
	
	//-----------------------------------------------------------------------
	//		Constructors
	//-----------------------------------------------------------------------
	public Overload(int num)
	{
		this.num = num;
	}
	
	public Overload()
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
