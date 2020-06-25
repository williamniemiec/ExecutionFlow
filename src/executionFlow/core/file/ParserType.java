package executionFlow.core.file;


/**
 * Contains all types of parser supported.
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		1.5
 * @since		1.5
 */
public enum ParserType 
{
	//-------------------------------------------------------------------------
	//		Enumerations
	//-------------------------------------------------------------------------
	INVOKER("INVOKER"), 
	TEST_METHOD("TEST_METHOD"),
	PRE_TEST_METHOD("PRE_TEST_METHOD");
	
	
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	private String name;
	
	
	//-------------------------------------------------------------------------
	//		Constructor
	//-------------------------------------------------------------------------
	private ParserType(String name)
	{
		this.name = name;
	}
	
	
	//-------------------------------------------------------------------------
	//		Getters
	//-------------------------------------------------------------------------
	public String getName()
	{
		return name;
	}
}
