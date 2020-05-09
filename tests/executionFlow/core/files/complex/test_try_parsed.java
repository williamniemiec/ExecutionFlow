import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class test_try 
{
	public boolean tryCatchMethod_try(int num)
	{
		File f = new File("tmp");
		
		FileWriter fw;int _db04ee2473693b92ea40dc8836c5e892=0;
		try  {_db04ee2473693b92ea40dc8836c5e892=0;

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
		
		FileWriter fw;int _db04ee2473693b92ea40dc8836c5e892=0;
		try  {_db04ee2473693b92ea40dc8836c5e892=0;

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
