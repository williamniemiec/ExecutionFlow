package executionFlow;

import static java.lang.invoke.MethodType.methodType;

import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.util.List;

import info.ClassConstructorInfo;
import info.ClassMethodInfo;


public class MethodExecutionFlow 
{
	//-----------------------------------------------------------------------
	//		Attributes
	//-----------------------------------------------------------------------
	private ClassExecutionFlow classExecutionFlow;
	private String methodName;
	private List<Integer> methodPath;
	private String methodSignature;
	//private ClassConstructorInfo cci;
	private Object[] args;
	private Object instance;
	

	//-----------------------------------------------------------------------
	//		Constructor
	//-----------------------------------------------------------------------
	//public MethodExecutionFlow(ClassExecutionFlow classExecutionFlow, ClassMethodInfo cmi, ClassConstructorInfo cci) 
	public MethodExecutionFlow(ClassExecutionFlow classExecutionFlow, ClassMethodInfo cmi, Object instance)
	{
		this.classExecutionFlow = classExecutionFlow;
		this.methodName = cmi.getMethodName();
		this.args = cmi.getArgs();
		this.methodSignature = cmi.getSignature();
		this.instance = instance;
		//this.cci = cci;
	}


	//-----------------------------------------------------------------------
	//		Getters
	//-----------------------------------------------------------------------
	public List<Integer> getMethodPath() 
	{ 
		return methodPath; 
	}
	
	private MethodType getMethodType() 
	{
		Method m = classExecutionFlow.getMethod(methodSignature);
		Class<?>[] params = m.getParameterTypes();
		
		return methodType(m.getReturnType(), params);
	}
	
	
	//-----------------------------------------------------------------------
	//		Methods
	//-----------------------------------------------------------------------
	/**
	 * Starts the execution of the method and add executed lines in
	 * {@link #executionPath}
	 * 
	 * @param args Parameter's values of the method
	 * @throws Throwable 
	 * @throws IllegalStateException if {@link #classSignature} is empty
	 */
	public MethodExecutionFlow execute() throws Throwable 
	{
		if (methodName == null) { return this; }
		
		MethodType mt = getMethodType();
		methodPath = CheapCoverage.getExecutionPath(methodName, mt, args, instance);

		return this;
	}
}
