package executionFlow.runtime.controlFlow;

public class IfElse 
{
	public String ifElseMethod(int num)
	{
		String response = "";
		
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
