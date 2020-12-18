package executionFlow.info;


/**
 * Stores information about a method or constructor along with a test method.
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		5.2.3
 * @since		5.2.3
 */
public class InvokedContainer 
{
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	private InvokedInfo invokedInfo;
	private InvokedInfo testMethodInfo;
	
	
	//-------------------------------------------------------------------------
	//		Constructor
	//-------------------------------------------------------------------------
	/**
	 * Stores information about a method or constructor along with a test method.
	 * 
	 * @param		invokedInfo Information about a method or constructor
	 * @param		testMethodInfo Information about a test method
	 */
	public InvokedContainer(InvokedInfo invokedInfo, InvokedInfo testMethodInfo)
	{
		this.invokedInfo = invokedInfo;
		this.testMethodInfo = testMethodInfo;
	}
	

	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	@Override
	public String toString() 
	{
		return "CollectorInfo ["
				+ "invokedInfo=" + invokedInfo 
				+ ", testMethodInfo=" + testMethodInfo
			+ "]";
	}

	
	//-------------------------------------------------------------------------
	//		Getters
	//-------------------------------------------------------------------------
	public InvokedInfo getInvokedInfo() 
	{
		return invokedInfo;
	}
	
	public InvokedInfo getTestMethodInfo() 
	{
		return testMethodInfo;
	}
}
