package executionflow.info;

import java.io.Serializable;

/**
 * Stores information about a method or constructor along with a test method.
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		6.0.0
 * @since		6.0.0
 */
public class InvokedContainer implements Serializable {
	
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	private static final long serialVersionUID = 600L;
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
	 * 
	 * @throws		IllegalArgumentException If invoked info or test method info
	 * is null
	 */
	public InvokedContainer(InvokedInfo invokedInfo, InvokedInfo testMethodInfo) {
		if (invokedInfo == null)
			throw new IllegalArgumentException("Invoked info cannot be null");
		
		if (testMethodInfo == null)
			throw new IllegalArgumentException("Test method info cannot be null");
		
		this.invokedInfo = invokedInfo;
		this.testMethodInfo = testMethodInfo;
	}
	

	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	@Override
	public String toString() {
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
		
		if (!invokedInfo.equals(other.invokedInfo))
			return false;
		
		if (!testMethodInfo.equals(other.testMethodInfo))
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
	public InvokedInfo getInvokedInfo() {
		return invokedInfo;
	}
	
	public InvokedInfo getTestMethodInfo() {
		return testMethodInfo;
	}
}
