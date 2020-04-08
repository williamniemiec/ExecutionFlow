package executionFlow;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import executionFlow.exporter.ConsoleExporter;
import executionFlow.exporter.ExporterExecutionFlow;
import executionFlow.exporter.FileExporter;
import executionFlow.info.ClassConstructorInfo;
import executionFlow.info.ClassMethodInfo;
import executionFlow.info.SignaturesInfo;


/**
 * Given a class path and specific methods calculate the execution path for each
 * of these methods. This is the main class of execution flow.
 */
@SuppressWarnings("unused")
public class ExecutionFlow 
{
	//-----------------------------------------------------------------------
	//		Attributes
	//-----------------------------------------------------------------------
	private ClassExecutionFlow classExecutionFlow;
	private Map<SignaturesInfo, List<Integer>> classPaths = new HashMap<>();
	private ClassConstructorInfo cci;
	private List<ClassMethodInfo> methods = new ArrayList<>();
	private ExporterExecutionFlow exporter;
	
	
	//-----------------------------------------------------------------------
	//		Initialization block
	//-----------------------------------------------------------------------
	/**
	 * Defines how the export will be done.
	 */
	{
		//exporter = new ConsoleExporter(classPaths);
		exporter = new FileExporter(classPaths);
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
		
		// Generates the test path for each method that was provided in the constructor
		for (ClassMethodInfo method : methods) {
			methodPath = new ArrayList<>();
			mef = new MethodExecutionFlow(classExecutionFlow, method, cci);
			
			methodPath.addAll( mef.execute().getMethodPath() );
			classPaths.put(extractSignatures(method), methodPath);
			
		}
		
		return this;
	}
	
	/**
	 * Exports the result.
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
	
	/**
	 * Extracts the types of the method parameters.
	 * 
	 * @param parametersTypes Types of each method's parameter
	 * @return String with the name of each type separated by commas
	 */
	private String extractParameterTypes(Class<?>[] parametersTypes)
	{
		StringBuilder parameterTypes = new StringBuilder();
		
		for (var parameterType : parametersTypes) {
			parameterTypes.append(parameterType.getTypeName() +",");
		}
		
		if (parameterTypes.length() > 0)
			parameterTypes.deleteCharAt(parameterTypes.length()-1);	// Removes last comma
		
		return parameterTypes.toString();
	}
	
	
	//-----------------------------------------------------------------------
	//		Getters & Setters
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
	
	public ExecutionFlow setExporter(ExporterExecutionFlow exporter) 
	{
		this.exporter = exporter;
		
		return this;
	}
}
