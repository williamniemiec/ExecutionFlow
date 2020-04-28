package executionFlow.runtime.controlFlow;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class TestClass_ControlFlow 
{
	public String ifElseMethod(int num)
	{
		if (num < 0) {
			return "";
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
			case 'Á':
			case 'À':
			case 'Ã':
			case 'Â':
				letter = 'A';
				break;
			case 'É':
			case 'È':
			case 'Ê':
				letter = 'E';
				break;
			case 'Ì':
			case 'Í':
			case 'Î':
				letter = 'I';
				break;
			case 'Ò':
			case 'Ó':
			case 'Ô':
			case 'Õ':
				letter = 'O';
				break;
			case 'Ú':
			case 'Ù':
			case 'Û':
				letter = 'U';
				break;
		}
		
		return letter;
	}
}
