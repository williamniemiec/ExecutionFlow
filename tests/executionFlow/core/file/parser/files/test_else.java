package executionFlow.core.file.parser.files;
public class test_else 
{
	public String ifElseMethod(int num)
	{
		if (num < 0) {
			return "";
		}
		
		String response = "";
		int w;
        int k;

		if (num == 0) {
			response = "Number zero";
		} else if (num == 1) {
			response = "Number one";
		} else {
			response = "Number "+num;
		}
		
		return response;
	}
}