package executionFlow.runtime.testClasses;


/**
 * Created to test classes that implements an interface.
 */
public class ClassInterface implements Interface
{
	//-----------------------------------------------------------------------
	//		Attributes
	//-----------------------------------------------------------------------
	private String text;
	
	
	//-----------------------------------------------------------------------
	//		Constructor
	//-----------------------------------------------------------------------
	public ClassInterface(String text)
	{
		this.text = text;
	}
	
	
	//-----------------------------------------------------------------------
	//		Methods
	//-----------------------------------------------------------------------
	@Override
	public String test()
	{
		int x = 2;
		return text;
	}
}
