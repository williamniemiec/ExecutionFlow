package executionFlow.info;


/**
 * Stores signatures of a method. The signatures are:
 * <li>Method's signature</li>
 * <li>Test method's signature</li>
 * 
 * @author William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @since 1.0
 * @version 1.4
 */
public class SignaturesInfo 
{
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	private String methodSignature;
	private String testMethodSignature;
	
	
	//-------------------------------------------------------------------------
	//		Constructor
	//-------------------------------------------------------------------------
	/**
	 * Stores signatures of a method.
	 * 
	 * @param methodSignature Method's signature
	 * @param testMethodSignature Test method's signature
	 */
	public SignaturesInfo(String methodSignature, String testMethodSignature) 
	{
		this.methodSignature = methodSignature;
		this.testMethodSignature = testMethodSignature;
	}
	
	
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	/**
	 * Returns test method signature and method signature in the following format:<br />
	 * <code>test_method_signature + '$' + method_signature</code>
	 */
	@Override
	public String toString()
	{
		return testMethodSignature+"$"+methodSignature;
	}
	
	
	//-------------------------------------------------------------------------
	//		Getters
	//-------------------------------------------------------------------------
	public String getMethodSignature() 
	{
		return methodSignature;
	}
	
	public String getTestMethodSignature() 
	{
		return testMethodSignature;
	}
}
