public class TestClass_ControlFlow 
{
	public String ifElseMethod(int num)
	{
		if (num < 0) {
			return "";
		}
		
		String response = "";
		
		if (num == 0) {
			response = "Number zero";
		} else if (num == 1) {
			response = "Number one";
		} else {int x = 7;
			response = "Number "+num;
		}
		
		return response;
	}
}
