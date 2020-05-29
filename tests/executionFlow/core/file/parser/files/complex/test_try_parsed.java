package executionFlow.core.file.parser.files.complex;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class test_try 
{
	public boolean tryCatchMethod_try(int num)
	{
		File f = new File("tmp");
		
		FileWriter fw;int _67c2529999d7657f44093a5b44e8a63a=0;
		try  {int _2705e0b24328778009b93c4411e582d6=0;

			fw = new FileWriter(f);
			fw.write('x');
			fw.close();
			f.delete();
		} catch (IOException e) 
		{
			return false;
		}
		
		return true;
	}
	
	public boolean tryCatchMethod_try2(int num)
	{
		File f = new File("tmp");
		
		FileWriter fw;int _bb2cb0da29c6cd022bd29081387b41e3=0;
		try  {int _dd35dabd05659d2e400e108b8f8898f9=0;

			fw = new FileWriter(f);
			fw.write('x');
			fw.close();
			f.delete();
		} 
		catch (IOException e) 
		{
			return false;
		}
		
		return true;
	}
}
