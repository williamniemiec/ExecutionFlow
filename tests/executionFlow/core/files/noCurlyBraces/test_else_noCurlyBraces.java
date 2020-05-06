public class TestClass_ControlFlow 
{
	public String ifElseMethod(int num)
	{
		if (num < 0)
			return "";
		
		String response = "";
		int w;
        int k;

		if (num == 0)
			response = "Number zero";
		else if (num == 1)
			response = "Number one";
		else 
			if (k)
				k++;
			else if (y) y++
			else
				if (true) {
					k++;
					y++;
				}
				else
					false;
		
		return response;
	}
}