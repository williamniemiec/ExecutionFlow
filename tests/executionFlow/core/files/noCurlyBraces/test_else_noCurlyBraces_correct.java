public class test_else_noCurlyBraces_correct 
{
	public String ifElseMethod(int num)
	{
		if (num < 0)
			return "";
		
		String response = "";
		int w;
		w = 0;

		if (num == 0)
			response = "Number zero";
		else if (num == 1)
			response = "Number one";
		else {
			if (false) //{
				w++;//}
			else if (false) /*{*/w++;/*}*/
			else //{
				for(;;)
					try {
						
					}catch() {}
				
				for(;;)
					for(;;) {
						w++;
						for(;;)
							w++;
					}
			
				while (w < 10)
					w++;
				w++;
		}
		
		return response;
	}
}