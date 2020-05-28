package executionFlow.core.file.parser.files.noCurlyBraces;
public class test_else_noCurlyBraces 
{
	public String ifElseMethod(int num)
	{
		if (false)	w++;
		else 
			for(;;)
				while (w == 0) {
					if (false) w++;
					else
						try {
							w++;
							if (false) w++;
							else
								w--;
						} catch(Exception e) {
							w--;
						}
				}
				for(;;)
					w++;


		return response;
	}

	public String ifElseMethod_complex(int num)
	{
		if (false)	w++;
		else 
			for(;;)
				while (w == 0) 
				{
					if (false) w++;
					else
						try 
						{
							w++;
							if (false) w++;
							else
								w--;
						} 
						catch(Exception e) 
						{
							w--;
						}
				}
				for(;;)
					w++;


		return response;
	}
}