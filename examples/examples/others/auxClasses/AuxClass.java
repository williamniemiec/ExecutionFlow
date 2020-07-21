package examples.others.auxClasses;

import java.util.Arrays;
import java.util.List;



/**
 * Class created for aspect tests.
 */
public class AuxClass 
{
	//-----------------------------------------------------------------------
	//		Attributes
	//-----------------------------------------------------------------------
	public String text;
	private int num;
	
	
	//-----------------------------------------------------------------------
	//		Constructors
	//-----------------------------------------------------------------------
	public AuxClass(String t, int x) 
	{
		this.text = t;
	}
	
	/**
	 * Random constructor.
	 */
	public AuxClass(int num) 
	{
		this.num = num;
	}
	
	/**
	 * Random constructor.
	 */
	public AuxClass(int x, int y) 
	{
		x = 2;
	}
	
	/**
	 * Random constructor.
	 */
	public AuxClass(String x) 
	{
		x = "2";
	}
	
	
	//-----------------------------------------------------------------------
	//		Methods
	//-----------------------------------------------------------------------
	/**
	 * Random function.
	 */
	public void test(int x, int y, String k) 
	{
		x = 3;
	}
	
	/**
	 * Empty concrete function.
	 */
	public void test2() 
	{ }
	
	public static void test3(int x, int y, String k) 
	{
		x = 0;
		while (x < 3) {
			x++;
		}
		
		x = 3;
	}
	
	/**
	 * Empty static function.
	 */
	public static void test4() { }
	
	/**
	 * Calculates the factorial of a number.
	 * 
	 * @param x Number you want to know the factorial
	 * @return Factorial of the number
	 */
	public long factorial(int x) 
	{
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
	 * @return x-th fibonacci term
	 */
	public long fibonacci(int x) 
	{
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
	
	/**
	 * Inverts letters without calling a function.
	 * 
	 * @param letters Letters that will be inverted.
	 * @return Inverted letters
	 */
	public static char[] parseLetters_noInternalCall(CharSequence cs)
	{
		char[] letters = cs.toString().toCharArray();
		char[] response = new char[letters.length];
		
		for (int i=0; i<letters.length; i++) {
			if (letters[i] == Character.toUpperCase(letters[i]))
				response[i] =  Character.toLowerCase(letters[i]);
			else {
				response[i] =  Character.toUpperCase(letters[i]);}
		}
		
		return response;
	}
	
	
	/**
	 * Inverts letters by calling a function.
	 * 
	 * @param letters Letters that will be inverted..
	 * @return Inverted letters
	 */
	public static char[] parseLetters_withInternalCall(char[] letters) 
	{
		char[] response = new char[letters.length];
		
		for (int i=0; i<letters.length; i++) {
			// Internal call - should not be considered the path made within this call
			response[i] = invertLetter(letters[i]);
		}
		
		return response;
	}
	
	/**
	 * Turns a lowercase letter to an uppercase letter and vice versa.
	 * 
	 * @param letter Letter to be inverted
	 * @return The letter inverted
	 */
	private static char invertLetter(char letter) 
	{
		if (letter == Character.toUpperCase(letter)) {
			return Character.toLowerCase(letter);
		}
		
		return Character.toUpperCase(letter);
	}
	
	/**
	 * Method with three possible paths.
	 * 
	 * @param num Number between 1 and 2
	 * @return Number in text or an empty string if the number is not between 1 and 2
	 */
	public String threePaths(int num)
	{
		if (num == 1) {
			return "one";
		}
		
		if (num == 2) {
			return "two";
		}
		
		return "";
	}

	public int testObjParam(Object obj)
	{
		return obj.hashCode();
	}
	
	public int getNumber()
	{
		return num;
	}
	
	public List<Integer> identity(int num1, int num2, int num3, int num4, int num5)
	{
		List<Integer> response = Arrays.asList(
				num1,
				num2,
				num3,
				num4,
				num5
		);
		
		return response;
	}
}
