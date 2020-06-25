package executionFlow.info;


/**
 * Stores invoker signature, where invoker can be a method or a
 * constructor. The signatures are:
 * <ul>
 * 	<li>Invoker signature</li>
 * 	<li>Signature of the test method to which this invoker is called</li>
 * </ul>
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		1.5
 * @since		1.0
 */
public class SignaturesInfo 
{
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	private String invokerSignature;
	private String testMethodSignature;
	
	
	//-------------------------------------------------------------------------
	//		Constructor
	//-------------------------------------------------------------------------
	/**
	 * Stores signatures of an invoker (method or constructor), where an 
	 * invoker can be a method or a constructor.
	 * 
	 * @param		invokerSignature Invoker signature
	 * @param		testMethodSignature Test method's signature
	 */
	public SignaturesInfo(String invokerSignature, String testMethodSignature) 
	{
		this.invokerSignature = invokerSignature;
		this.testMethodSignature = testMethodSignature;
	}
	
	
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	/**
	 * Returns test method signature and invoker signature in the following 
	 * format: <br />
	 * <code>test_method_signature + '$' + invoker_signature</code>
	 */
	@Override
	public String toString()
	{
		return testMethodSignature + "$" + invokerSignature;
	}

	@Override
	public int hashCode() 
	{
		return this.invokerSignature.hashCode() + this.testMethodSignature.hashCode();
	}

	@Override
	public boolean equals(Object obj) 
	{
		if (obj == null)						{	return false;	}
		if (obj.getClass() != this.getClass())	{	return false;	}
		if (this == obj)						{	return true;	}
		
		SignaturesInfo si = (SignaturesInfo) obj;
		
		
		return	this.invokerSignature.equals(si.getInvokerSignature()) && 
				this.testMethodSignature.equals(si.getTestMethodSignature());
	}


	//-------------------------------------------------------------------------
	//		Getters
	//-------------------------------------------------------------------------
	public String getInvokerSignature() 
	{
		return invokerSignature;
	}
	
	public String getTestMethodSignature() 
	{
		return testMethodSignature;
	}
}
