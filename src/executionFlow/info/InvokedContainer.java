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
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		
		result = prime * result + ((invokedInfo == null) ? 0 
				: invokedInfo.getConcreteInvokedSignature().hashCode());
		result = prime * result + ((testMethodInfo == null) ? 0 
				: testMethodInfo.getInvokedSignature().hashCode());
		
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		
		if (obj == null)
			return false;
		
		if (getClass() != obj.getClass())
			return false;
		
		InvokedContainer other = (InvokedContainer) obj;
		
		if (invokedInfo == null) {
			if (other.invokedInfo != null)
				return false;
		} 
		else if (!invokedInfo.equals(other.invokedInfo))
			return false;
		
		if (testMethodInfo == null) {
			if (other.testMethodInfo != null)
				return false;
		} 
		else if (!testMethodInfo.equals(other.testMethodInfo))
			return false;
		
		if (!invokedInfo.getConcreteInvokedSignature().equals(
				other.invokedInfo.getConcreteInvokedSignature()))
			return false;
		
		return testMethodInfo.getConcreteInvokedSignature().equals(
				other.testMethodInfo.getConcreteInvokedSignature());
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
