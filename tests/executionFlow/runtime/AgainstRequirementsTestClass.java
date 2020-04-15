package executionFlow.runtime;


public class AgainstRequirementsTestClass 
{
	//-----------------------------------------------------------------------
	//		Attributes
	//-----------------------------------------------------------------------
	private boolean skip;
	
	
	//-----------------------------------------------------------------------
	//		Constructors
	//-----------------------------------------------------------------------
	public AgainstRequirementsTestClass(boolean skip)
	{
		this.skip = skip;
	}
	
	public AgainstRequirementsTestClass()
	{
		this.skip = false;
	}
	
	
	//-----------------------------------------------------------------------
	//		Methods
	//-----------------------------------------------------------------------
	/**
	 * Calculates the factorial of a number.
	 * 
	 * @param x Number you want to know the factorial
	 * @return Factorial of the number or -1 if skip is true
	 */
	public long factorial(int x) 
	{
		if (skip)
			return -1;
		
		long response = 1;
		
		for (int i=1; i<=x; i++) {
			response *= i;
		}
		
		return response;
	}
	
	/**
	 * Calculates x-th fibonacci term.
	 * 
	 * @param x Fibonacci index term
	 * @return x-th fibonacci term or -1 if skip is true
	 */
	public long fibonacci(int x) 
	{
		if (skip)
			return -1;
		
		long response = 1;
		long last = 1;
		long aux = 0;

		for (int i=2; i<x; i++) {
			aux = response;
			response = response + last;
			last = aux;
		}
		
		return response;
	}
	
	public static long staticFactorial(int x) 
	{	
		long response = 1;
		
		for (int i=1; i<=x; i++) {
			response *= i;
		}
		
		return response;
	}
}
