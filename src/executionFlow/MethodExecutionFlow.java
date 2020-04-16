package executionFlow;

import java.util.List;

import executionFlow.core.CheapCoverage;
import executionFlow.info.ClassConstructorInfo;
import executionFlow.info.ClassMethodInfo;
import executionFlow.info.CollectorInfo;


/**
 * Manage method manipulation (extract data that {@link ExecutionFlow} will need)
 */
public class MethodExecutionFlow 
{
	//-----------------------------------------------------------------------
	//		Attributes
	//-----------------------------------------------------------------------
	private List<Integer> methodPath;
	private ClassConstructorInfo constructorInfo;
	private ClassMethodInfo methodInfo;
	

	//-----------------------------------------------------------------------
	//		Constructor
	//-----------------------------------------------------------------------
	/**
	 * @param classExecutionFlow Information about the collected methods
	 */
	public MethodExecutionFlow(CollectorInfo ci)
	{
		methodInfo = ci.getMethodInfo();
		constructorInfo = ci.getConstructorInfo();
	}
	
	
	//-----------------------------------------------------------------------
	//		Methods
	//-----------------------------------------------------------------------
	/**
	 * Starts the execution of the method and add executed lines in
	 * {@link #methodPath}
	 * 
	 * @param args Parameter's values of the method
	 * @throws Throwable If an error occurs in class processing
	 */
	public MethodExecutionFlow execute() throws Throwable 
	{
		if (methodInfo.getMethodName() == null) { return this; }
		
		CheapCoverage.loadClass(methodInfo.getClassPath());
		methodPath = CheapCoverage.getTestPath(methodInfo, constructorInfo);
		
		return this;
	}
	
	
	//-----------------------------------------------------------------------
	//		Getters
	//-----------------------------------------------------------------------
	public List<Integer> getMethodPath() 
	{ 
		return methodPath; 
	}
}
