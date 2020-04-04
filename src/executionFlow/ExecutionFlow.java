package executionFlow;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import executionFlow.exporter.ConsoleExporter;
import executionFlow.exporter.ExporterExecutionFlow;
import executionFlow.info.ClassConstructorInfo;
import executionFlow.info.ClassMethodInfo;
import executionFlow.info.SignaturesInfo;


/**
 * Given a class path and specific methods calculate the execution path for each
 * of these methods. This is the main class of execution flow.
 */
public class ExecutionFlow 
{
	//-----------------------------------------------------------------------
	//		Attributes
	//-----------------------------------------------------------------------
	private ClassExecutionFlow classExecutionFlow;
	//private Map<ClassMethodInfo, List<Integer>> classPaths = new HashMap<>();
	private Map<SignaturesInfo, List<Integer>> classPaths = new HashMap<>();
	private ClassConstructorInfo cci;
	private List<ClassMethodInfo> methods = new ArrayList<>();
	final ExporterExecutionFlow exporter;
	
	{
		exporter = new ConsoleExporter(classPaths);
	}
	
	
	//-----------------------------------------------------------------------
	//		Constructors
	//-----------------------------------------------------------------------
	/**
	 * Given a class path and specific methods calculate the execution path for each
	 * of these methods.
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
	 * Returns method's execution path.
	 * 
	 * @return Map where key is method's signature and value is method's test path
	 */
	public Map<String, List<Integer>> getClassPaths() 
	{
		Map<String, List<Integer>> response = new HashMap<>();
		
		for (Map.Entry<SignaturesInfo, List<Integer>> entry : classPaths.entrySet()) {
			SignaturesInfo signatures = entry.getKey();
			
			response.put(signatures.getMethodSignature(), entry.getValue());
		}
		
		return response; 
	}
	

	//-----------------------------------------------------------------------
	//		Methods
	//-----------------------------------------------------------------------
	/**
	 * Walks the method recording its execution path and save the result in
	 * {@link #classPaths}.
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
			classPaths.put(extractSignatures(method), methodPath);
			
		}
		return this;
	}
	
	/**
	 * Show info on display, where info is:
	 * <li>Method's signature</li>
	 * <li>Method's execution path</li>
	 * 
	 * @implNote This method will be changed to export to a file
	 */
	public void export() 
	{
		exporter.export();
	}
	

	/**
	 * Extracts test method's signature and method's signature.
	 * 
	 * @param cmi Method that signatures will be obtained
	 * @return {@link SignaturesInfo} with the signatures
	 */
	private SignaturesInfo extractSignatures(ClassMethodInfo cmi)
	{
		Method m = classExecutionFlow.getMethod(cmi.getSignature());
		String parameterTypes = extractParameterTypes(m.getParameterTypes());
		
		String methodSignature = classExecutionFlow.getClassSignature()+"."+m.getName()+"("+parameterTypes+")";
		String testMethodSignature = cmi.getTestMethodSignature();
		
		return new SignaturesInfo(methodSignature, testMethodSignature);
	}
	
	
	private String extractParameterTypes(Class<?>[] parameters)
	{
		StringBuilder parameterTypes = new StringBuilder();
		
		for (var parameterType : parameters) {
			parameterTypes.append(parameterType.getTypeName() +",");
		}
		
		if (parameterTypes.length() > 0)
			parameterTypes.deleteCharAt(parameterTypes.length()-1);	// Remove last comma
		
		return parameterTypes.toString();
	}
}
