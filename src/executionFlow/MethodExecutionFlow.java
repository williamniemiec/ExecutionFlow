package executionFlow;

import static java.lang.invoke.MethodType.methodType;

import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.util.List;
import executionFlow.cheapCoverage.CheapCoverage;
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
//	private String methodSignature;
//	private String methodName;
	private List<Integer> methodPath;
//	private Object[] args;
//	private ClassExecutionFlow classExecutionFlow;
//	private ClassConstructorInfo cci;
//	private String classFilePath;
//	private Class<?>[] typeArgs;
//	private Class<?> returnType;
	
	private ClassConstructorInfo constructorInfo;
	private ClassMethodInfo methodInfo;
	

	//-----------------------------------------------------------------------
	//		Constructor
	//-----------------------------------------------------------------------
	public MethodExecutionFlow(CollectorInfo ci)
	{
		methodInfo = ci.getMethodInfo();
		constructorInfo = ci.getConstructorInfo();
	}
	
	
//	public MethodExecutionFlow(ClassMethodInfo cmi)
//	{
//		this.methodSignature = cmi.getSignature();
//		this.methodName = cmi.getMethodName();
//		this.typeArgs = cmi.getParameterTypes();
//		this.args = cmi.getArgs();
//		//this.classExecutionFlow = classExecutionFlow;
//		this.cci = cmi.getClassConstructorInfo();
//		this.classFilePath = cmi.getClassPath();
//		this.returnType = cmi.getReturnTyoe();
//	}
//	
//	
//	
//	/**
//	 * @param classExecutionFlow Information about the class that the method belongs
//	 * @param cmi Information about the method
//	 * @param cci Information about the constructor of the class that the method belongs
//	 */
//	public MethodExecutionFlow(ClassExecutionFlow classExecutionFlow, ClassMethodInfo cmi, ClassConstructorInfo cci)
//	{
//		this.methodSignature = cmi.getSignature();
//		this.methodName = cmi.getMethodName();
//		this.args = cmi.getArgs();
//		this.classExecutionFlow = classExecutionFlow;
//		this.cci = cci;
//	}
	
	
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
		
		//MethodType mt = getMethodType();
		
		CheapCoverage.loadClass(methodInfo.getClassPath());
//		methodPath = CheapCoverage.getExecutionPath(methodName, mt, args, cci);
		methodPath = CheapCoverage.getExecutionPath(methodInfo, constructorInfo);
		
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
