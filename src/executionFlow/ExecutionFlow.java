package executionFlow;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import info.ClassConstructorInfo;
import info.ClassMethodInfo;


/**
 * Given a class path and specific methods calculate the execution path for each
 * of these methods
 */
public class ExecutionFlow 
{
	//-----------------------------------------------------------------------
	//		Attributes
	//-----------------------------------------------------------------------
	private ClassExecutionFlow classExecutionFlow;
	private Map<Method, List<Integer>> classPaths = new HashMap<>();
	private List<ClassMethodInfo> methods;
	private ClassConstructorInfo classConstructorInfo;
	
	
	//-----------------------------------------------------------------------
	//		Constructors
	//-----------------------------------------------------------------------
	/**
	 * Given a class path and specific methods calculate the execution path for each
	 * of these methods
	 * 
	 * @param classPath Path of the class, including ".class" in the end
	 * @param cmi List of {@link ClassMethodInfo methods} to be analyzed
	 * @param cci Information about the constructor of {@link classPath} (necessary
	 * if there are non static methods in {@link ClassMethodInfo the list of methods})
	 */
	public ExecutionFlow(String classPath, List<ClassMethodInfo> cmi, ClassConstructorInfo cci) 
	{
		this.classExecutionFlow = new ClassExecutionFlow(classPath);
		this.methods = cmi;
		this.classConstructorInfo = cci;
	}
	
	/**
	 * Given a class path and specific methods calculate the execution path for each
	 * of these methods
	 * 
	 * @param classPath Path of the class, including ".class" in the end
	 * @param cmi List of {@link ClassMethodInfo methods} to be analyzed
	 * @implNote If there are non static methods you also have to pass {@link ClassConstructorInfo}
	 */
	public ExecutionFlow(String classPath, List<ClassMethodInfo> cmi) 
	{
		this(classPath, cmi, null);
	}
	
	
	//-----------------------------------------------------------------------
	//		Getters & Setters
	//-----------------------------------------------------------------------
	/**
	 * Give method's execution path
	 * 
	 * @return Map where key is method's signature and value is method's execution path
	 */
	public Map<String, List<Integer>> getClassPaths() 
	{
		Map<String, List<Integer>> response = new HashMap<>();
		
		for (Map.Entry<Method, List<Integer>> entry : classPaths.entrySet()) {
			Method m = entry.getKey();
			StringBuilder parameterTypes = new StringBuilder();
			
			for (var parameterType : m.getParameterTypes()) {
				parameterTypes.append(parameterType.getTypeName() +",");
			}
			
			if (parameterTypes.length() > 0)
				parameterTypes.deleteCharAt(parameterTypes.length()-1);	// Remove last comma
			
			String signature = classExecutionFlow.getClassSignature()+"."+m.getName()+"("+parameterTypes+")";
			
			response.put(signature, entry.getValue());
		}
		
		return response; 
	}
	
	public void setMethodPath(Method method, List<Integer> path) {
		classPaths.put(method, path);
	}
	

	//-----------------------------------------------------------------------
	//		Methods
	//-----------------------------------------------------------------------
	public ExecutionFlow execute() throws Throwable {
		// Percorre tds metodos
		List<Integer> methodPath = new ArrayList<>();
		MethodExecutionFlow mef;
		
		//for (Map.Entry<String, Object[]> entry : methods.entrySet()) {
		for (ClassMethodInfo method : methods) {
			methodPath = new ArrayList<>();
			
			// Para cada metodo calcula seu path
			mef = new MethodExecutionFlow(classExecutionFlow, method, classConstructorInfo);
			
			methodPath.addAll( mef.execute().getMethodPath() );
			classPaths.put(classExecutionFlow.getMethod(method.getSignature()), methodPath);
		}
		
		return this;
	}
	
	/**
	 * Show info on display, where info is:
	 * <li>Method's signature</li>
	 * <li>Method's execution path</li>
	 */
	public void export() 
	{
		System.out.println("---------------------------------------------------------------------");
		System.out.println("                                EXPORT                               ");
		System.out.println("---------------------------------------------------------------------");
		for (Map.Entry<Method, List<Integer>> e : classPaths.entrySet()) {
			Method m = e.getKey();
			StringBuilder parameterTypes = new StringBuilder();
			
			for (var parameterType : m.getParameterTypes()) {
				parameterTypes.append(parameterType.getTypeName() +",");
			}
			
			if (parameterTypes.length() > 0)
				parameterTypes.deleteCharAt(parameterTypes.length()-1);	// Remove last comma
			
			String signature = classExecutionFlow.getClassSignature()+"."+m.getName()+"("+parameterTypes+")";
			
			System.out.println(signature);
			System.out.println(e.getValue());
			System.out.println();
		}
	}
}
