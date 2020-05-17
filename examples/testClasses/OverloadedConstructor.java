package testClasses;

public class OverloadedConstructor 
{
	private int num;
	
	public OverloadedConstructor(int num)
	{
		this.num = num;
	}
	
	public OverloadedConstructor()
	{
		this(0);
	}
	
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
