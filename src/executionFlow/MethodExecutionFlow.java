package executionFlow;

import static java.lang.invoke.MethodType.methodType;

import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.util.List;
import executionFlow.cheapCoverage.CheapCoverage;
import executionFlow.info.ClassConstructorInfo;
import executionFlow.info.ClassMethodInfo;


/**
 * Manage method manipulation (extract data that {@link ExecutionFlow} will need)
 */
public class MethodExecutionFlow 
{
	//-----------------------------------------------------------------------
	//		Attributes
	//-----------------------------------------------------------------------
	private String methodSignature;
	private String methodName;
	private List<Integer> methodPath;
	private Object[] args;
	private ClassExecutionFlow classExecutionFlow;
	private ClassConstructorInfo cci;
	

	//-----------------------------------------------------------------------
	//		Constructor
	//-----------------------------------------------------------------------
	/**
	 * @param classExecutionFlow Information about the class that the method belongs
	 * @param cmi Information about the method
	 * @param cci Information about the constructor of the class that the method belongs
	 */
	public MethodExecutionFlow(ClassExecutionFlow classExecutionFlow, ClassMethodInfo cmi, ClassConstructorInfo cci)
	{
		this.methodSignature = cmi.getSignature();
		this.methodName = cmi.getMethodName();
		this.args = cmi.getArgs();
		this.classExecutionFlow = classExecutionFlow;
		this.cci = cci;
	}


	//-----------------------------------------------------------------------
	//		Getters
	//-----------------------------------------------------------------------
	public List<Integer> getMethodPath() 
	{ 
		return methodPath; 
	}
	
	/**
	 * @return Return type and parameter types of the method
	 */
	private MethodType getMethodType() 
	{
		Method m = classExecutionFlow.getMethod(methodSignature);
		Class<?>[] params = m.getParameterTypes();
		
		if (params.length == 0)
			return methodType(m.getReturnType());
		
		return methodType(m.getReturnType(), params);
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
		if (methodName == null) { return this; }
		
		MethodType mt = getMethodType();
		methodPath = CheapCoverage.getExecutionPath(methodName, mt, args, cci);
		
		return this;
	}
}
