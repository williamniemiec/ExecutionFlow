package wniemiec.app.executionflow.invoked;

import java.io.Serializable;

/**
 * Stores information about a tested method or constructor along with the test
 * method that test them.
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @since		7.0.0
 */
public class TestedInvoked implements Serializable {
	
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	private static final long serialVersionUID = 700L;
	private Invoked testedInvoked;
	private Invoked testMethod;
	
	
	//-------------------------------------------------------------------------
	//		Constructor
	//-------------------------------------------------------------------------
	/**
	 * Stores information about a method or constructor along with the test
	 * method that test them.
	 * 
	 * @param		testedInvoked Information about a tested method or 
	 * constructor
	 * @param		testMethod Information about the test method that test the
	 * method or constructor
	 * 
	 * @throws		IllegalArgumentException If invoked or test method is null
	 */
	public TestedInvoked(Invoked testedInvoked, Invoked testMethod) {
		if (testedInvoked == null)
			throw new IllegalArgumentException("Tested invoked cannot be null");
		
		if (testMethod == null)
			throw new IllegalArgumentException("Test method cannot be null");
		
		this.testedInvoked = testedInvoked;
		this.testMethod = testMethod;
	}
	

	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	@Override
	public String toString() {
		return "TestedInvoked ["
				+ "testedInvoked=" + testedInvoked 
				+ ", testMethod=" + testMethod
			+ "]";
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		
		result = prime * result + ((testedInvoked == null) ? 0 
				: testedInvoked.getConcreteSignature().hashCode());
		result = prime * result + ((testMethod == null) ? 0 
				: testMethod.getInvokedSignature().hashCode());
		
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
		
		TestedInvoked other = (TestedInvoked) obj;
		
		if (!testedInvoked.equals(other.testedInvoked))
			return false;
		
		if (!testMethod.equals(other.testMethod))
			return false;
		
		if (!testedInvoked.getConcreteSignature().equals(
				other.testedInvoked.getConcreteSignature()))
			return false;
		
		return testMethod.getConcreteSignature().equals(
				other.testMethod.getConcreteSignature());
	}


	//-------------------------------------------------------------------------
	//		Getters
	//-------------------------------------------------------------------------
	public Invoked getTestedInvoked() {
		return testedInvoked;
	}
	
	public Invoked getTestMethod() {
		return testMethod;
	}
}
