package executionFlow.runtime;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import executionFlow.info.ClassMethodInfo;
import executionFlow.info.ClassConstructorInfo;
import org.junit.Test;
import executionFlow.*;
import executionFlow.cheapCoverage.CheapCoverage;
import executionFlow.cheapCoverage.RT;
import executionFlow.info.ClassConstructorInfo;
import executionFlow.info.ClassMethodInfo;
import executionFlow.exporter.*;


/**
 * Captures all executed methods with <code>@Test</code> annotation, including
 * inner methods (captures the method and all internal calls to other methods).
 * 
 * @implNote Excludes calls to native java methods and ExecutionFlow's classes
 */
@SuppressWarnings("unused")
public aspect MethodCollector extends RuntimeCollector
{
	//-----------------------------------------------------------------------
	//		Pointcut
	//-----------------------------------------------------------------------
	pointcut methodCollector(): (cflow(execution(@Test * *.*(*))) || cflow(call(* *.*(*)))) 
								 && !within(RuntimeCollector)
								 && !within(TestMethodCollector)
								 && !within(MethodCollector)
								 && !within(ConstructorCollector)
								 && !within(CollectorExecutionFlow)
								 && !within(ClassMethodInfo)
								 && !within(ClassConstructorInfo)
								 && !within(MethodExecutionFlow)
								 && !within(ClassExecutionFlow)								 
								 && !within(ExecutionFlow)
								 && !within(CheapCoverage)
								 && !within(RT)
								 && !within(ConsoleExporter)
								 && !within(FileExporter)
								 && !call(* org.junit.runner.JUnitCore.runClasses(*))
								 && !call(void org.junit.Assert.*(*,*));
	
	/**
	 * Executed before the end of each internal call of a method with @Test annotation
	 */
	before(): methodCollector()
	{	
		// Ignores if the class has @SkipCollection annotation
		if (hasSkipCollectionAnnotation(thisJoinPoint)) { return; }
		
		String signature = thisJoinPoint.getSignature().toString();
				
		// Ignores native java methods
		if (isNativeMethod(signature)) { return; }

		// Ignores methods in the method test (with @Test) (it will only consider internal calls)
		if (testClassSignature != null && signature.contains(testClassSignature)) { return; }	
		
		// Checks if is a method signature
		if (!isMethodSignature(signature)) { return; }
		
		// Extracts the method name
		String methodName = CollectorExecutionFlow.extractClassName(signature);
		
		// Extracts types of method parameters (if there is any)
		Class<?>[] paramTypes = CollectorExecutionFlow.extractParamTypes(thisJoinPoint.getArgs());
		
		// Gets class path
		try {
			classPath = CollectorExecutionFlow.findCurrentClassPath();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		Map<String, String> methodInfo = new HashMap<>();
		methodInfo.put(signature, Arrays.toString(thisJoinPoint.getArgs()));
		
		// If the method has already been collected, skip it;
		if (methodCollector.containsKey(methodInfo)) { return; }
		
		// Checks if it is an internal call (if it is, ignore it)
		if (isInternalCall(signature)) { return; }
		
		// Collects the method
		ClassMethodInfo cmi = new ClassMethodInfo(testMethodSignature, methodName, paramTypes, thisJoinPoint.getArgs());
		methodCollector.put(methodInfo, cmi);
		lastInsertedMethod = signature;
	}
	
	
	//-----------------------------------------------------------------------
	//		Methods
	//-----------------------------------------------------------------------
	/**
	 * Checks if a signature belong to an internal call.
	 * 
	 * @param signature Signature of the method
	 * @return If it belong to an internal call
	 */
	private boolean isInternalCall(String signature)
	{
		// Gets class package
		String classPackage = CollectorExecutionFlow.extractPackageName(signature);
		
		return !lastInsertedMethod.equals(signature) && lastInsertedMethod.contains(classPackage);
	}
}
