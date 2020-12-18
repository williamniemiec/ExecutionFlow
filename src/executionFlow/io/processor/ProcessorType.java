package executionFlow.io.processor;


/**
 * Contains all types of supported processors.
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		2.0.0
 * @since		2.0.0
 */
public enum ProcessorType 
{
	//-------------------------------------------------------------------------
	//		Enumerations
	//-------------------------------------------------------------------------
	INVOKED("INVOKED"), 
	TEST_METHOD("TEST_METHOD"),
	PRE_TEST_METHOD("PRE_TEST_METHOD");
	
	
	//-------------------------------------------------------------------------
	//		Attributes'
	//-------------------------------------------------------------------------
	private String name;
	
	
	//-------------------------------------------------------------------------
	//		Constructor
	//-------------------------------------------------------------------------
	private ProcessorType(String name)
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
