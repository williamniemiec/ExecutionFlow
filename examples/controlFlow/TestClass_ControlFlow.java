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
			case 'Á':x=2;
			case 'À':x=2;
			case 'Ã':x=2;
			case 'Â':x=2;
				letter = 'A';
				break;
			case 'É':x=2;
			case 'È':x=2;
			case 'Ê':x=2;
				letter = 'E';
				break;
			case 'Ì':x=2;
			case 'Í':x=2;
			case 'Î':x=2;
				letter = 'I';
				break;
			case 'Ò':x=2;
			case 'Ó':x=2;
			case 'Ô':x=2;
			case 'Õ':x=2;
				letter = 'O';
				break;
			case 'Ú':x=2;
			case 'Ù':x=2;
			case 'Û':x=2;
				letter = 'U';
				break;
		}
		
		return letter;
	}
}
