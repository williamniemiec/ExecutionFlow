package executionFlow.info;


/**
 * Stores information about a method, constructor and a test method.
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
	 * Stores {@link MethodInvokedInfo information} about a method.
	 */
	private InvokedInfo methodInfo;
	
	/**
	 * Stores {@link MethodInvokedInfo information} about a test method.
	 */
	private InvokedInfo testMethodInfo;
	
	/**
	 * Stores {@link ConstructorInvokedInfo information} about a constructor.
	 */
	private InvokedInfo constructorInfo;
	
	
	//-------------------------------------------------------------------------
	//		Constructor
	//-------------------------------------------------------------------------
	/**
	 * Stores information about a method, constructor and test method.
	 * 
	 * @param		methodInfo Information about a method
	 * @param		constructorInfo Information about a constructor
	 * @param		testMethodInfo Information about a test method
	 */
	private CollectorInfo(InvokedInfo methodInfo, InvokedInfo constructorInfo, 
			InvokedInfo testMethodInfo)
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
	public static class Builder
	{
		private InvokedInfo methodInfo;
		private InvokedInfo testMethodInfo;
		private InvokedInfo constructorInfo;
		
		
		/**
		 * @param		methodInfo Informations about a method
		 * 
		 * @return		Builder to allow chained calls
		 * 
		 * @throws		IllegalArgumentException If methodInfo is null
		 */
		public Builder methodInfo(InvokedInfo methodInfo)
		{
			if (methodInfo == null)
				throw new IllegalArgumentException("Method's info cannot be null");
			
			this.methodInfo = methodInfo;
			
			return this;
		}
		
		/**
		 * @param		testMethodInfo Informations about a test method
		 * 
		 * @return		Builder to allow chained calls
		 * 
		 * @throws		IllegalArgumentException If testMethodInfo is null
		 */
		public Builder testMethodInfo(InvokedInfo testMethodInfo)
		{
			if (testMethodInfo == null)
				throw new IllegalArgumentException("Test method's info cannot be null");
			
			this.testMethodInfo = testMethodInfo;
			
			return this;
		}
		
		/**
		 * @param		constructorInfo Informations about a constructor
		 * 
		 * @return		Builder to allow chained calls
		 * 
		 * @throws		IllegalArgumentException If constructorInfo is null
		 */
		public Builder constructorInfo(InvokedInfo constructorInfo)
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
	public InvokedInfo getMethodInfo() 
	{
		return methodInfo;
	}
	
	public InvokedInfo getTestMethodInfo() 
	{
		return testMethodInfo;
	}
	
	public InvokedInfo getConstructorInfo() 
	{
		return constructorInfo;
	}
}
