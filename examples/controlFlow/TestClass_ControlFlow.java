package controlFlow;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class TestClass_ControlFlow 
{
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
		} else {int x = 7;
			response = "Number "+num;
		}
		
		return response;
	}
	
	public boolean tryCatchMethod_try()
	{
		File f = new File("tmp");
		
		int x=7;FileWriter fw;
		try {x=7;
			fw = new FileWriter(f);
			fw.write('x');
			fw.close();
			f.delete();
		} catch (IOException e) {
			return false;
		}
		
		return true;
	}
	
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
				
		int x;switch (letter) {
			case '�':x=2;
			case '�':x=2;
			case '�':x=2;
			case '�':x=2;
				letter = 'A';
				break;
			case '�':x=2;
			case '�':x=2;
			case '�':x=2;
				letter = 'E';
				break;
			case '�':x=2;
			case '�':x=2;
			case '�':x=2;
				letter = 'I';
				break;
			case '�':x=2;
			case '�':x=2;
			case '�':x=2;
			case '�':x=2;
				letter = 'O';
				break;
			case '�':x=2;
			case '�':x=2;
			case '�':x=2;
				letter = 'U';
				break;
		}
		
		return letter;
	}
}
