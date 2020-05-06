import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class test_doWhile
{
	public void doWhile(int num)
	{
		int i = 0, k = 0;

		do 
		{
			k++;
		} while (i < num);
	}
	
	public void doWhile2(int num)
	{
		int i = 0, k = 0;

		do 
		{
			k++;
		} 
		while (i < num);
	}
}