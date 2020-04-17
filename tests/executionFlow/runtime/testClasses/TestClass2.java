package executionFlow.runtime.testClasses;


/**
 * Class created for aspect tests.
 */
public class TestClass2 
{
	//-----------------------------------------------------------------------
	//		Attributes
	//-----------------------------------------------------------------------
	private int num;
	
	
	//-----------------------------------------------------------------------
	//		Constructor
	//-----------------------------------------------------------------------
	public TestClass2(int num) 
	{
		this.num = num;
	}
	
	
	//-----------------------------------------------------------------------
	//		Methods
	//-----------------------------------------------------------------------
	/**
	 * Calculates the factorial of a number.
	 * 
	 * @return Factorial of the number
	 */
	public long factorial() 
	{
		long response = 1;
		
		for (int i=1; i<=num; i++) {
			response *= i;
		}
		
		return response;
	}
}
