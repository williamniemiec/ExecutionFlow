public class TestClass_ControlFlow 
{
	public String ifElseMethod(int num)
	{
		if (num < 0) {
			return "";
		}
		
		String response = "";
		int w;int x=7;
        int k;x=7;

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
