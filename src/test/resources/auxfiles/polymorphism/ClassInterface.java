package auxfiles.polymorphism;


/**
 * Created for inheritance and polymorphism tests.
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
	public String interfaceMethod()
	{
		System.out.println("interfaceMethod()");
		return text;
	}
	
	public static boolean testClassParam(ClassInterface ci)
	{
		return true;
	}
}
