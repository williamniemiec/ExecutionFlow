package executionFlow.info;


/**
 * Stores signatures of a method. The signatures are:
 * <li>Method's signature</li>
 * <li>Test method's signature</li>
 */
public class SignaturesInfo 
{
	//-----------------------------------------------------------------------
	//		Attributes
	//-----------------------------------------------------------------------
	private String methodSignature;
	private String testMethodSignature;
	
	
	//-----------------------------------------------------------------------
	//		Constructor
	//-----------------------------------------------------------------------
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
	
	
	//-----------------------------------------------------------------------
	//		Getters
	//-----------------------------------------------------------------------
	public String getMethodSignature() 
	{
		return methodSignature;
	}
	
	public String getTestMethodSignature() 
	{
		return testMethodSignature;
	}
}
