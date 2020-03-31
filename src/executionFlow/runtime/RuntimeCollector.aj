package executionFlow.runtime;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import executionFlow.ExecutionFlow;
import executionFlow.info.ClassConstructorInfo;
import executionFlow.info.ClassMethodInfo;


/**
 * Responsible for data collection of methods and class builders used in tests<hr />
 * <h2>Requirements</h2>
 * <li>Each test only uses one constructor of the class to be tested (consequently there 
 * will only be one class path per test)</li>
 * <li>Each test only tests methods of a class / constructor</li>
 * <li>Each test must have <code>@Test</code> annotation</li>
 */
public aspect RuntimeCollector {
	//-----------------------------------------------------------------------
	//		Attributes
	//-----------------------------------------------------------------------
	private static Map<String, ClassMethodInfo> methodCollector = new HashMap<>();
	private static Map<String, ClassConstructorInfo> consCollector = new HashMap<>();
	private static String classPath;
	private static ClassConstructorInfo cci;
	private static boolean firstTime = true;
	
	
	//-----------------------------------------------------------------------
	//		Pointcuts
	//-----------------------------------------------------------------------
	/**
	 * Captures all executed methods with <code>@Test</code> annotation, not including
	 * internal calls.
	 */
	pointcut pc3(): execution(@Test * *.*(*));
	after(): pc3() 		// Executed after the end of a method with @Test annotation
	{
		// Reset firstTime flag
		firstTime = true;
		
		// Show method execution path
		ExecutionFlow ef = new ExecutionFlow(classPath, methodCollector.values(), cci);
		try {
			ef.execute().export();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Captures all executed methods with <code>@Test</code> annotation, including
	 * inner methods (captures the method and all internal calls to other methods).
	 * 
	 * @implNote Excludes calls to native java methods and ExecutionFlow's classes
	 */
	pointcut pc2(): cflow(execution(@Test * *.*(*))) && !within(ClassMethodInfo) 
													 && !within(ClassConstructorInfo)
													 && !within(RuntimeCollector) 
													 && !within(CollectorExecutionFlow) 
													 && !within(ExecutionFlow) 
													 && !within(ClassExecutionFlow)
													 && !within(MethodParam)
													 && !within(RT)
													 && !within(CheapCoverage);
	before(): pc2()		// Executed before the end of each internal call of a method with @Test annotation
	{
		if (firstTime) {			// Ignores the external method (with @Test annotation) of the collection
			firstTime = false;
			return; 
		}
		
		String signature = thisJoinPoint.getSignature().toString();
		
		if (signature.contains("java.")) { return; }		// Ignores native java methods
		
		String methodRegex = "[A-z]+\\s([A-z0-9-_$]+\\.)+[A-z0-9-_$]+\\([A-z0-9-_$,\\s]*\\)";
		String constructorRegex = "[^\\s\\t]([A-z0-9-_$]+\\.)*[A-z0-9-_$]+\\([A-z0-9-_$,\\s]*\\)";
		
		// Collect constructor data
		Class<?>[] consParamTypes;		// Constructor parameter types
		Object[] consParamValues;		// Constructor parameter values
		
		// Checks if it is a constructor signature and if it has not been collected yet
		if (signature.matches(constructorRegex) && !consCollector.containsKey(signature)) {
			// Extracts constructor data
			consParamTypes = CollectorExecutionFlow.extractParamTypes(thisJoinPoint.getArgs());
			consParamValues = thisJoinPoint.getArgs();
			
			// Save extracted data
			consCollector.put(signature, new ClassConstructorInfo(consParamTypes, consParamValues));
			cci = new ClassConstructorInfo(consParamTypes, consParamValues);
		}
		
		// Check if is a method signature
		if (signature.matches(methodRegex)){
			// Extract the method name
			String methodName = CollectorExecutionFlow.extractClassName(signature);
			
			// Extract types of method parameters (if any)
			Class<?>[] paramTypes = CollectorExecutionFlow.extractParamTypes(thisJoinPoint.getArgs());
			
			// Checks if the method has already been collected
			if (methodCollector.containsKey(signature)) {
				// If it was, get its ClassMethodInfo
				ClassMethodInfo cmi = methodCollector.get(signature);
				
				// Gets class path and save its in the method
				try {
					classPath = CollectorExecutionFlow.findCurrentClassPath();
					cmi.setClassPath(classPath);		
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {	// If the method has not been collected, collect it
				 ClassMethodInfo cmi = new ClassMethodInfo(methodName, paramTypes, thisJoinPoint.getArgs());
				 methodCollector.put(signature, cmi);
			}			
		}
	}
}
