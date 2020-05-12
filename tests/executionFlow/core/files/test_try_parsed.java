package executionFlow.core.files;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class test_try 
{
	public boolean tryCatchMethod_try(int num)
	{
		File f = new File("tmp");
		
		FileWriter fw;int _fe46e5c4af5ae39fd79a35d0857d47e0=0;
		try {_fe46e5c4af5ae39fd79a35d0857d47e0=0;
			fw = new FileWriter(f);
			fw.write('x');
			fw.close();
			f.delete();
		} catch (IOException e) {
			return false;
		}
		
		return true;
	}
}
