package Math;


public class CalculatorNonStatic 
{
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
		
		while (k < 3) {
			k++;
		}
	}
}
