package chainedCalls;


/**
 * Class that allows chained methods.
 */
public class Calculator 
{
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	private float ans;
	
	
	//-------------------------------------------------------------------------
	//		Constructors
	//-------------------------------------------------------------------------
	/**
	 * Class that allows chained methods. Simulates a calculator.
	 * 
	 * @param number Initial number
	 */
	public Calculator(int number)
	{
		ans = number;
	}
	
	/**
	 * Class that allows chained methods. Simulates a calculator. Using this
	 * constructor, initial value will be zero.
	 */
	public Calculator()
	{
		this(0);
	}
	
	
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------	
	public Calculator setNumber(float num)
	{
		ans = num;
		return this;
	}
	
	public Calculator sum(float number) 
	{
		ans += number;
		return this;
	}
	
	public Calculator sub(float number) 
	{
		ans -= number;
		return this;
	}
	
	public Calculator mult(float number) 
	{
		ans *= number;
		return this;
	}

	public Calculator div(float number) 
	{
		ans /= number;
		return this;
	}
	
	public Calculator showAnswer() 
	{
		System.out.println(ans);
		return this;
	}
	
	public float ans()
	{
		return ans;
	}	
}
