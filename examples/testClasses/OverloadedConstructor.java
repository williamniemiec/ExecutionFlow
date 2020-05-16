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
		return num;
	}
	
	public int overloadedMethod()
	{
		int response= overloadedMethod(-1);
		
		int x = 2;
		x++;
		x++;
		
		return response;
	}
}
