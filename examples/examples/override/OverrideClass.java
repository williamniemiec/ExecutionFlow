package examples.override;


/**
 * Class created for {@link OverrideTest} test.
 */
public class OverrideClass 
{
	public OverrideClass()
	{
		System.out.println("cons");
	}
	
	//-----------------------------------------------------------------------
	//		Methods
	//-----------------------------------------------------------------------
	public int foo(int num)
	{
		num *= 10;
		return num;
	}
}
