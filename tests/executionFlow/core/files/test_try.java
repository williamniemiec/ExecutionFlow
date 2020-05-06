import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class test_try 
{
	public boolean tryCatchMethod_try(int num)
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
}