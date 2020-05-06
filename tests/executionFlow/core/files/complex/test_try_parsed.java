import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class test_try 
{
	public boolean tryCatchMethod_try(int num)
	{
		File f = new File("tmp");
		
		FileWriter fw;int _3484944859822f01de95018843b1ed2e=7;
		try  {_3484944859822f01de95018843b1ed2e=7;

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
		
		FileWriter fw;_3484944859822f01de95018843b1ed2e=7;
		try  {_3484944859822f01de95018843b1ed2e=7;

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
