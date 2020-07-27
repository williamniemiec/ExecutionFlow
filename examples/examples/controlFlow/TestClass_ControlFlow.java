package examples.controlFlow;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


/**
 * Class with control flow methods.
 */
public class TestClass_ControlFlow 
{
	//-----------------------------------------------------------------------
	//		Methods
	//-----------------------------------------------------------------------
	/**
	 * Method with a if-else control flow. It returns if a number is negative,
	 * is the number zero or number one; otherwise, returns "Number &lt;number&gt;".
	 * 
	 * @param num A number
	 * @return "Negative value", "Number zero", "Number one" or "Number &lt;num&gt;"
	 */
	public String ifElseMethod(int num)
	{
		if (num < 0) {
			return "Negative value";
		}
		
		String response = "";
		
		if (num == 0) {
			response = "Number zero";
		} else if (num == 1) {
			response = "Number one";
		} else {
			response = "Number "+num;
		}
		
		return response;
	}
	
	/**
	 * Method with a try-catch control flow. Never generates 
	 * {@link IOException}; always returns true.
	 * 
	 * @return true
	 */
	public boolean tryCatchMethod_try()
	{
		File f = new File("tmp");
		
		FileWriter fw;
		try {
			fw = new FileWriter(f);
			fw.write('x');
			fw.close();
			f.delete();
		} catch (IOException e) {
			return false;
		}
		
		return true;
	}
	
	/**
	 * Method with a try-catch control flow. Always generates 
	 * {@link IOException}, catch it and returns false.
	 * 
	 * @return false
	 */
	public boolean tryCatchMethod_catch()
	{
		File f = new File("tmp");
		
		FileWriter fw;
		try {
			throw new IOException();
		} catch (IOException e) {
			return false;
		}
	}
	
	/**
	 * Removes accentuation from a letter.
	 * 
	 * @param letter Letter that will have its accentuation removed
	 */
	public char switchCaseMethod(char letter)
	{ 
		letter = Character.toUpperCase(letter);
				
		switch (letter) {
		case '�':
			case '�':
			case '�':
			case '�':
			case '�':
				letter = 'A';
				break;
			case '�':
			case '�':
			case '�':
				letter = 'E';
				break;
			case '�':
			case '�':
			case '�':
				letter = 'I';
				break;
			case '�':
			case '�':
			case '�':
			case '�':
				letter = 'O';
				break;
			case '�':
			case '�':
			case '�':
				letter = 'U';
				break;
		}
		
		return letter;
	}
	
	/**
	 * Method with a do-while control flow. It returns the sum of two numbers.
	 * 
	 * @param a A number
	 * @param b Another number
	 * @return a + b
	 */
	public int doWhileMethod(int a, int b)
	{
		if (a == 0)
			return b;
		if (b == 0)
			return a;
		
		do {
			a++;
			b--;
		} while (b != 0);
		
		return a;	// a = a + b;
	}
	
	/**
	 * Method with an inline while.
	 * 
	 * @param n A number
	 * @return Zero
	 */
	public int inlineWhile(int a)
	{
		while (a > 0) a--;
		
		return a;
	}
	
	/**
	 * Method with an inline if-else.
	 * 
	 * @param n A number
	 * @return Zero
	 */
	public boolean inlineIfElse(int n)
	{
		if (n > 0) return true; else return false; 
	}
	
	/**
	 * Method with an inline do-while.
	 * 
	 * @param n A number
	 * @return Zero
	 */
	public int inlineDoWhile(int n)
	{
		if (n <= 0)
			n = 1;
		
		do { n--; } while (n > 0);
		
		return n;
	}
	
	/**
	 * Method with an if clause along with else in the same line.
	 * 
	 * @param		num
	 * 
	 * @return		0 if num > 0; otherwise, returns 10
	 */
	public int ifElseSameLine(int num)
	{
		if (num > 0) { num *= 0; } else {
			num = 10;
		}
		
		return num;
	}
}