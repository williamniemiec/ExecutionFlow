package executionFlow.core.file.parser.files;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class test_try 
{
	public boolean tryCatchMethod_try(int num)
	{
		File f = new File("tmp");
		
		FileWriter fw;int _91036746f0c28b950ea8db90cf9dca09=0;
		try {int _f5e2b7a9208bacbacfab2e82abc0cbf3=0;
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
