package chainedCalls;

public class Calculator 
{
	private float ans;
	
	public Calculator(int number)
	{
		ans = number;
	}
	
	public Calculator()
	{
		this(0);
	}
	
	public Calculator showAnswer() 
	{
		System.out.println(ans);
		return this;
	}
	
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
	
	public float ans()
	{
		return ans;
	}
}
