package Math;


public class CalculatorNonStaticWithConstructor 
{
	private int x;
	
	
	public CalculatorNonStaticWithConstructor(int x) {
		this.x = x;
	}
	
	
	public int sum(int a, int b) 
	{
		var sum = a;
		
		for (var x = 0; x < b; x++) {
			sum++;
		}
		
		return sum;
	}
	
	public void loop() 
	{
		int k = 0;
		
		while (k < x) {
			k++;
		}
	}
}
