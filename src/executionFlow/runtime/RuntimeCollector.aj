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
	private static String testClassSignature;
	
	public void finalize() 
	{
		ExecutionFlow ef = new ExecutionFlow(classPath, methodCollector.values(), cci);
		try {
			ef.execute().export();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
	
	
	//-----------------------------------------------------------------------
	//		Pointcuts
	//-----------------------------------------------------------------------
	/**
	 * Captures all executed methods with <code>@Test</code> annotation, not including
	 * internal calls.
	 */
	pointcut pc3(): execution(void executionFlow.runtime.JUnitTest.*(*)) && !within(RuntimeCollector);
	after() returning(): pc3() 		// Executed after the end of a method with @Test annotation
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
	pointcut pc2(): (cflow(execution(@Test * *.*(*))) || cflow(call(* *.*(*)))) && !within(ClassMethodInfo) 
													 && !within(ClassConstructorInfo)
													 && !within(RuntimeCollector) 
													 && !within(CollectorExecutionFlow) 
													 && !within(ExecutionFlow) 
													 && !within(ClassExecutionFlow)
													 && !within(RT)
													 && !within(CheapCoverage)
													 && !call(* org.junit.runner.JUnitCore.runClasses(*))
													 && !call(void org.junit.Assert.*(*,*));
	before(): pc2()		// Executed before the end of each internal call of a method with @Test annotation
	{
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
		if (signature.contains("java.")) { return; }		
		
		// Ignores methods in the method test (with @Test) (will only consider internal calls)
		if (signature.contains(testClassSignature)) { return; }	
		
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
				 
				System.out.println("sig added: "+signature);
			}			
		}
	}
}
