import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class test_doWhile
{
	public void doWhile(int num)
	{
		int i = num;

		do {
			if (i < 0)
				break;
			i--;
		} while (i > 0);
	}
}