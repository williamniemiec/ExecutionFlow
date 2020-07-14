package executionFlow.info;


/**
 * Stores information about a method and the test method it belongs to.
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		2.0.0
 * @since		1.0
 */
public class CollectorInfo 
{
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	/**
	 * Stores {@link MethodInvokerInfo information} about a method.
	 */
	private InvokerInfo methodInfo;
	
	/**
	 * Stores {@link MethodInvokerInfo information} about a test method.
	 */
	private InvokerInfo testMethodInfo;
	
	/**
	 * Stores {@link ConstructorInvokerInfo information} about the constructor of
	 * {@link #methodInfo the method}.
	 */
	private InvokerInfo constructorInfo;
	
	
	//-------------------------------------------------------------------------
	//		Constructor
	//-------------------------------------------------------------------------
	/**
	 * Stores information about a method, its constructor, along with its order
	 * and test method to which this method is called.
	 * 
	 * @param		methodInfo Information about a method
	 * @param		testMethodInfo Information about the test method to which 
	 * the method is called
	 */
	private CollectorInfo(InvokerInfo methodInfo, InvokerInfo constructorInfo, 
			InvokerInfo testMethodInfo)
	{
		this.methodInfo = methodInfo;
		this.constructorInfo = constructorInfo;
		this.testMethodInfo = testMethodInfo;
	}
	
	
	//-------------------------------------------------------------------------
	//		Builder
	//-------------------------------------------------------------------------
	/**
	 * Builder for {@link CollectorInfo}. It is necessary to provide at least 
	 * one of the following fields: <br />
	 * <ul>
	 * 	<li>methodInfo</li>
	 * 	<li>testMethodInfo</li>
	 * 	<li>constructorInfo</li>
	 * </ul>
	 */
	public static class CollectorInfoBuilder
	{
		private InvokerInfo methodInfo;
		private InvokerInfo testMethodInfo;
		private InvokerInfo constructorInfo;
		
		
		/**
		 * @param		methodInfo Informations about a method
		 * @return		Builder to allow chained calls
		 * @throws		IllegalArgumentException If methodInfo is null
		 */
		public CollectorInfoBuilder methodInfo(InvokerInfo methodInfo)
		{
			if (methodInfo == null)
				throw new IllegalArgumentException("Method's info cannot be null");
			
			this.methodInfo = methodInfo;
			
			return this;
		}
		
		/**
		 * @param		testMethodInfo Informations about a test method
		 * @return		Builder to allow chained calls
		 * @throws		IllegalArgumentException If testMethodInfo is null
		 */
		public CollectorInfoBuilder testMethodInfo(InvokerInfo testMethodInfo)
		{
			if (testMethodInfo == null)
				throw new IllegalArgumentException("Test method's info cannot be null");
			
			this.testMethodInfo = testMethodInfo;
			
			return this;
		}
		
		/**
		 * @param		constructorInfo Informations about a constructor
		 * @return		Builder to allow chained calls
		 * @throws		IllegalArgumentException If constructorInfo is null
		 */
		public CollectorInfoBuilder constructorInfo(InvokerInfo constructorInfo)
		{
			if (constructorInfo == null)
				throw new IllegalArgumentException("Constructor's info cannot be null");
			
			this.constructorInfo = constructorInfo;
			
			return this;
		}
		
		/**
		 * Creates {@link CollectorInfo} instance. It is necessary to provide 
		 * at least one of the following fields: <br />
		 * <ul>
		 * 	<li>methodInfo</li>
		 * 	<li>testMethodInfo</li>
		 * 	<li>constructorInfo</li>
		 * </ul>
		 * 
		 * @throws		IllegalArgumentException If all fields above are null
		 */
		public CollectorInfo build()
		{
			if (methodInfo == null && testMethodInfo == null && constructorInfo == null)
				throw new IllegalArgumentException("It is necessary to provide "
						+ "at least one of the following fields: methodInfo, "
						+ "testMethodInfo or constructorInfo");
				
			return new CollectorInfo(methodInfo, constructorInfo, testMethodInfo);
		}
	}
	

	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	@Override
	public String toString() 
	{
		return "CollectorInfo ["
				+ "methodInfo=" + methodInfo 
				+ ", testMethodInfo=" + testMethodInfo 
				+ ", constructorInfo=" + constructorInfo
			+ "]";
	}

	
	//-------------------------------------------------------------------------
	//		Getters
	//-------------------------------------------------------------------------
	public InvokerInfo getMethodInfo() 
	{
		return methodInfo;
	}
	
	public InvokerInfo getTestMethodInfo() 
	{
		return testMethodInfo;
	}
	
	public InvokerInfo getConstructorInfo() 
	{
		return constructorInfo;
	}
}
