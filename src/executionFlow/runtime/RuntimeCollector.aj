package executionFlow.runtime;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

import executionFlow.ClassExecutionFlow;
import executionFlow.ExecutionFlow;
import executionFlow.cheapCoverage.CheapCoverage;
import executionFlow.cheapCoverage.RT;
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
@SuppressWarnings("unused")
public aspect RuntimeCollector 
{
	//-----------------------------------------------------------------------
	//		Attributes
	//-----------------------------------------------------------------------
	private static Map<String, ClassMethodInfo> methodCollector = new HashMap<>();
	private static Map<String, ClassConstructorInfo> consCollector = new HashMap<>();
	private static String classPath;
	private static ClassConstructorInfo cci;
	private static boolean firstTime = true;
	private static String testClassSignature;
	private static String testMethodSignature;
	private static String lastInsertedMethod = "";
	
	
	//-----------------------------------------------------------------------
	//		Methods
	//-----------------------------------------------------------------------
	/**
	 * Checks if there is the {@link @SkipCollection} annotation in the class
	 * 
	 * @param c Class to be analyzed
	 * @return If {@link @SkipCollection} annotation is present in the class;
	 */
	public boolean hasSkipCollectionAnnotation(Class<?> c)
	{
		if (c == null) { return false; }
		
		return c.isAnnotationPresent(SkipCollection.class);
	}
	
	/**
	 * Sets default value for all attributes
	 */
	public void reset()
	{
		methodCollector.clear();
		consCollector.clear();
		cci = null;
		firstTime = true;
		testClassSignature = null;
		testMethodSignature = null;
		lastInsertedMethod = "";
	}
	
	
	//-----------------------------------------------------------------------
	//		Pointcuts
	//-----------------------------------------------------------------------
	/**
	 * Captures class instantiation
	 */
	pointcut collectConstructor(): preinitialization(*.new(*)) 	&& !within(RuntimeCollector) 
																&& !within(CollectorExecutionFlow) 
																&& !within(ExecutionFlow) 
																&& !within(ClassExecutionFlow)
																&& !within(RT)
																&& !within(CheapCoverage)
																&& !call(* org.junit.runner.JUnitCore.runClasses(*))
																&& !call(void org.junit.Assert.*(*,*));
	after(): collectConstructor()
	{
		// Ignores if the class has @SkipCollection annotation
		if (thisJoinPoint.getThis() != null && hasSkipCollectionAnnotation(thisJoinPoint.getThis().getClass())) {
			return;
		}
		
		String signature = thisJoinPoint.getSignature().toString();
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
	}
	
	
	/**
	 * Captures all executed methods with <code>@Test</code> annotation, including
	 * inner methods (captures the method and all internal calls to other methods).
	 * 
	 * @implNote Excludes calls to native java methods and ExecutionFlow's classes
	 */
	pointcut pc2(): (cflow(execution(@Test * *.*(*))) || cflow(call(* *.*(*)))) && !within(ClassMethodInfo) 
													 && !within(ClassConstructorInfo)
													 && !within(RuntimeCollector) 
													 && !within(CollectorExecutionFlow) 
													 && !within(ExecutionFlow) 
													 && !within(ClassExecutionFlow)
													 && !cflow(call(* executionFlow.runtime.CollectorExecutionFlow.*(*)))
													 && !cflow(call(* executionFlow.info.ClassMethodInfo.*(*)))
													 && !cflow(call(* executionFlow.info.ClassConstructorInfo.*(*)))
													 && !cflow(call(* executionFlow.MethodExecutionFlow.*(*)))
													 && !cflow(call(* executionFlow.ExecutionFlow.*(*)))
													 && !cflow(call(* executionFlow.ClassExecutionFlow.*(*)))
													 && !cflow(call(* executionFlow.cheapCoverage.CheapCoverage.*(*)))
													 && !cflow(call(* executionFlow.cheapCoverage.RT.*(*)))
													 && !within(RT)
													 && !within(CheapCoverage)
													 && !call(* org.junit.runner.JUnitCore.runClasses(*))
													 && !call(void org.junit.Assert.*(*,*));
	before(): pc2()		// Executed before the end of each internal call of a method with @Test annotation
	{
		// Ignores if the class has @SkipCollection annotation
		if (thisJoinPoint.getThis() != null && hasSkipCollectionAnnotation(thisJoinPoint.getThis().getClass())) {
			return;
		}
		
		String signature = thisJoinPoint.getSignature().toString();
		
		// Ignores the external method (with @Test annotation) of the collection
		if (firstTime) {
			Pattern p = Pattern.compile("[A-z0-9-_$]+\\.\\<");
			Matcher m = p.matcher(signature);
			
			if (m.find()) {
				String name = m.group();
				p = Pattern.compile("[A-z0-9-_$]+");
				m = p.matcher(name);
				
				if (m.find()) {
					testClassSignature = m.group();
				}
			}
			
			firstTime = false;
			return; 
		}
				
		// Ignores native java methods
		if (signature == null || signature.contains("java.")) { return; }
		
		// Ignores methods in the method test (with @Test) (it will only consider internal calls)
		if (testClassSignature != null && signature.contains(testClassSignature)) { return; }	
		
		String methodRegex = "[A-z]+\\s([A-z0-9-_$]+\\.)+[A-z0-9-_$]+\\([A-z0-9-_$,\\s]*\\)";
		
		// Check if is a method signature
		if (signature.matches(methodRegex)){
			// Extract the method name
			String methodName = CollectorExecutionFlow.extractClassName(signature);
			
			// Extract types of method parameters (if there is any)
			Class<?>[] paramTypes = CollectorExecutionFlow.extractParamTypes(thisJoinPoint.getArgs());
			
			// Gets class path
			try {
				classPath = CollectorExecutionFlow.findCurrentClassPath();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			
			// If the method has not been collected, collect it
			if (!methodCollector.containsKey(signature)) {
				// Gets class package
				Pattern p = Pattern.compile("([A-z0-9\\-_$]+\\.)+");
				Matcher m = p.matcher(signature);
				String classPackage = "";

				if (m.find()) {
					String[] tmp = m.group().split("\\.");
					StringBuilder sb = new StringBuilder();
					
					for (int i=0; i<tmp.length-1; i++) {
						sb.append(tmp[i]);
						sb.append(".");
					}
					
					sb.deleteCharAt(sb.length()-1);		// Removes last dot
					classPackage = sb.toString();
				}
				
				// Checks if it is an internal call (if it is, ignore it)
				if (!lastInsertedMethod.contains(classPackage)) {
					ClassMethodInfo cmi = new ClassMethodInfo(testMethodSignature, methodName, paramTypes, thisJoinPoint.getArgs());
					methodCollector.put(signature, cmi);
					lastInsertedMethod = signature;
					
					// -----<DEBUG>-----
					// System.out.println("put: "+signature);
				}
			}
		}
	}
	
	/**
	 * Captures all executed methods with <code>@Test</code> annotation, not including
	 * internal calls.
	 */
	pointcut pc3(): execution(@Test * *.*()) && !within(RuntimeCollector);
	before(): pc3()
	{
		testMethodSignature = thisJoinPoint.getSignature().toString();
		testMethodSignature = testMethodSignature.substring(5);		// Removes return type
	}
	after(): pc3() 		// Executed after the end of a method with @Test annotation
	{	
		// Ignores if the class has @SkipCollection annotation
		if (hasSkipCollectionAnnotation(thisJoinPoint.getThis().getClass())) { return; }
		
		// Show method execution path
		ExecutionFlow ef = new ExecutionFlow(classPath, methodCollector.values(), cci);
		try {
			ef.execute().export();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		
		reset();	// Prepares for the next test
	}
}
