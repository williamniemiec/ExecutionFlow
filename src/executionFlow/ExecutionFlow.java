package executionFlow;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import executionFlow.info.ClassConstructorInfo;
import executionFlow.info.ClassMethodInfo;


/**
 * Given a class path and specific methods calculate the execution path for each
 * of these methods. This is the main class of execution flow
 */
public class ExecutionFlow 
{
	//-----------------------------------------------------------------------
	//		Attributes
	//-----------------------------------------------------------------------
	private ClassExecutionFlow classExecutionFlow;
	private Map<ClassMethodInfo, List<Integer>> classPaths = new HashMap<>();
	private ClassConstructorInfo cci;
	private List<ClassMethodInfo> methods = new ArrayList<>();
	
	
	//-----------------------------------------------------------------------
	//		Constructors
	//-----------------------------------------------------------------------
	/**
	 * Given a class path and specific methods calculate the execution path for each
	 * of these methods
	 * 
	 * @param classPath Path of the class, including ".class" in the end
	 * @param cmi List of {@link ClassMethodInfo methods} to be analyzed
	 //*@param cci Information about the constructor of {@link classPath} (necessary
	 * if there are non static methods in {@link ClassMethodInfo the list of methods})
	 */
	public ExecutionFlow(String classPath, Collection<ClassMethodInfo> cmi, ClassConstructorInfo cci) 
	{
		this(classPath, cmi);
		this.cci = cci;
	}
	
	/**
	 * Given a class path and specific methods calculate the execution path for each
	 * of these methods. Use this constructor if there are not concrete methods.
	 * 
	 * @param classPath Path of the class, including ".class" in the end
	 * @param cmi List of {@link ClassMethodInfo methods} to be analyzed
	 * @implNote If there is a non static methods you also have to pass {@link ClassConstructorInfo}
	 */
	public ExecutionFlow(String classPath, Collection<ClassMethodInfo> cmi) 
	{
		this.classExecutionFlow = new ClassExecutionFlow(classPath);
		this.methods.addAll(cmi);		// It is necessary to avoid ConcurrentModificationException
	}
	
	
	//-----------------------------------------------------------------------
	//		Getters
	//-----------------------------------------------------------------------
	/**
	 * Give method's execution path
	 * 
	 * @return Map where key is method's signature and value is method's execution path
	 */
	public Map<String, List<Integer>> getClassPaths() 
	{
		Map<String, List<Integer>> response = new HashMap<>();
		
		for (Map.Entry<ClassMethodInfo, List<Integer>> entry : classPaths.entrySet()) {
			ClassMethodInfo cmi = entry.getKey();
			Method m = classExecutionFlow.getMethod(cmi.getSignature());
			
			StringBuilder parameterTypes = new StringBuilder();
			
			for (Class<?> parameterType : m.getParameterTypes()) {
				parameterTypes.append(parameterType.getTypeName() +",");
			}
			
			if (parameterTypes.length() > 0)
				parameterTypes.deleteCharAt(parameterTypes.length()-1);		// Remove last comma
			
			String signature = classExecutionFlow.getClassSignature()+"."+m.getName()+"("+parameterTypes+")";
			
			response.put(signature, entry.getValue());
		}
		
		return response; 
	}
	

	//-----------------------------------------------------------------------
	//		Methods
	//-----------------------------------------------------------------------
	/**
	 * Walk the method recording its execution path and save the result in
	 * {@link #classPaths}
	 * 
	 * @return The instance (to allow chained calls)
	 * @throws Throwable If an error occurs
	 */
	public ExecutionFlow execute() throws Throwable 
	{
		List<Integer> methodPath = new ArrayList<>();
		MethodExecutionFlow mef;
		
		for (ClassMethodInfo method : methods) {
			methodPath = new ArrayList<>();
			mef = new MethodExecutionFlow(classExecutionFlow, method, cci);
			
			methodPath.addAll( mef.execute().getMethodPath() );
			classPaths.put(method, methodPath);
			
		}
		return this;
	}
	
	/**
	 * Show info on display, where info is:
	 * <li>Method's signature</li>
	 * <li>Method's execution path</li>
	 * @implNote This method will be changed to export to a file
	 */
	public void export() 
	{
		System.out.println("---------------------------------------------------------------------");
		System.out.println("                                EXPORT                               ");
		System.out.println("---------------------------------------------------------------------");
		for (Map.Entry<ClassMethodInfo, List<Integer>> e : classPaths.entrySet()) {
			ClassMethodInfo cmi = e.getKey();
			Method m = classExecutionFlow.getMethod(cmi.getSignature());
			
			StringBuilder parameterTypes = new StringBuilder();
			
			for (var parameterType : m.getParameterTypes()) {
				parameterTypes.append(parameterType.getTypeName() +",");
			}
			
			if (parameterTypes.length() > 0)
				parameterTypes.deleteCharAt(parameterTypes.length()-1);	// Remove last comma
			
			String signature = classExecutionFlow.getClassSignature()+"."+m.getName()+"("+parameterTypes+")";
			
			// Test method signature
			if (cmi.getTestMethodSignature() != null)
				System.out.println(cmi.getTestMethodSignature());
			
			System.out.println(signature);						// Method signature
			System.out.println(e.getValue());					// Test path
			System.out.println();
		}
	}
}
