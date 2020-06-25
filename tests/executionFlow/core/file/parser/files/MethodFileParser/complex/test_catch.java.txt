package executionFlow.core.file.parser.files.complex;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class test_catch
{
	public boolean tryCatchMethod_catch(int num)
	{
		File f = new File("tmp");
		
		FileWriter fw;
		try 
		{
			throw new IOException();
		} 
		catch (IOException e) 
		{
			return false;
		}
	}
}