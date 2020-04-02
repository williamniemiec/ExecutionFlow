package executionFlow.runtime;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import executionFlow.*;
import executionFlow.info.ClassConstructorInfo;
import executionFlow.info.ClassMethodInfo;
import executionFlow.cheapCoverage.*;
import org.junit.*;
import org.junit.Assert.*;
import org.junit.runner.JUnitCore;
import executionFlow.SkipCollection;


/**
 * Responsible for data collection of methods and class builders used in tests<hr />
 * <h2>Requirements</h2>
 * <li>Each test only uses one constructor of the class to be tested (consequently there 
 * will only be one class path per test)</li>
 * <li>Each test only tests methods of a class / constructor</li>
 * <li>Each test must have <code>@Test</code> annotation</li>
 */
@SuppressWarnings("unused")
public aspect RuntimeCollector {
	//-----------------------------------------------------------------------
	//		Attributes
	//-----------------------------------------------------------------------
	private static Map<String, ClassMethodInfo> methodCollector = new HashMap<>();
	private static Map<String, ClassConstructorInfo> consCollector = new HashMap<>();
	private static String classPath;
	private static ClassConstructorInfo cci;
	private static boolean firstTime = true;
	private static String testClassSignature;
	
	
	public boolean hasSkipCollectionAnnotation(Class<?> c)
	{
		if (c == null) { return false; }
		
		return c.isAnnotationPresent(SkipCollection.class);
	}
	
	//-----------------------------------------------------------------------
	//		Pointcuts
	//-----------------------------------------------------------------------
	/**
	 * Captures all executed methods with <code>@Test</code> annotation, not including
	 * internal calls.
	 */
	pointcut pc3(): execution(@Test * *.*()) && !within(RuntimeCollector);
	after() returning(): pc3() 		// Executed after the end of a method with @Test annotation
	{	
		//if (thisJoinPoint.getThis().getClass().isAnnotationPresent(SkipCollection.class)) { return; };
		if (hasSkipCollectionAnnotation(thisJoinPoint.getThis().getClass())) { return; }
		
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
		//if (thisJoinPoint.getThis().getClass().isAnnotationPresent(SkipCollection.class)) { return; };
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
			
			// Extract types of method parameters (if any)
			Class<?>[] paramTypes = CollectorExecutionFlow.extractParamTypes(thisJoinPoint.getArgs());
			
			// Gets class path (if has not been found yet)
			if (classPath == null) {
				try {
					classPath = CollectorExecutionFlow.findCurrentClassPath();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			
			// If the method has not been collected, collect it
			if (!methodCollector.containsKey(signature)) {
				ClassMethodInfo cmi = new ClassMethodInfo(methodName, paramTypes, thisJoinPoint.getArgs());
				methodCollector.put(signature, cmi);
				
				// -----<DEBUG>-----
				//System.out.println("put: "+signature);
			}
		}
	}
}
