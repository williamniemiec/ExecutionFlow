package executionFlow.runtime;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Arrays;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.Test;
import org.junit.jupiter.params.*;
import org.junit.jupiter.api.*;

import executionFlow.*;
import executionFlow.cheapCoverage.*;
import executionFlow.exporter.*;
import executionFlow.info.*;


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
	pointcut methodCollector(): 
		(cflow(execution(@Test * *.*())) || 
		 cflow(execution(@RepeatedTest * *.*())) ||
		 cflow(execution(@ParameterizedTest * *.*())) || 
		 cflow(execution(@TestFactory * *.*())) || 
		 cflow(call(* *.*(*))) || 
		 cflow(call(* *.*()))) 
		&& !within(RuntimeCollector)
		&& !within(TestMethodCollector)
		&& !within(MethodCollector)
		&& !within(ConstructorCollector) 
		&& !within(SignaturesInfo)
		&& !within(CollectorExecutionFlow)
		&& !within(ClassMethodInfo)
		&& !within(ClassConstructorInfo)
		&& !within(MethodExecutionFlow)
		&& !within(ClassExecutionFlow)	
		&& !within(CollectorInfo)
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
	after(): methodCollector()
	{	
		// Ignores if the class has @SkipCollection annotation
		if (hasSkipCollectionAnnotation(thisJoinPoint)) { return; }
		
		String signature = thisJoinPoint.getSignature().toString();
		
		// Ignores native java methods
		if (isNativeMethod(signature)) { return; }
		
		// Ignores methods in the method test (with @Test) (it will only consider internal calls)
		if (testMethodSignature != null && signature.contains(testMethodSignature)) { return; }
		
		// Checks if is a method signature
		if (!isMethodSignature(signature)) { return; }
		
		// Extracts the method name
		String methodName = CollectorExecutionFlow.extractClassName(signature);
		
		// Extracts types of method parameters (if there is any)
		Class<?>[] paramTypes = extractParameterTypes(thisJoinPoint);
		Class<?> returnType = extractReturnType(thisJoinPoint);		
		
		// Key is method's signature + values of method's parameters
		String key = signature+Arrays.toString(thisJoinPoint.getArgs());
		Object constructor = null;
		
		// Checks if there is a constructor (if it is a static method or not)
		if (thisJoinPoint.getThis() != null) {
//			if (thisJoinPoint.getThis().toString().contains(getClassName(testMethodSignature))) {
//				return;
//			}
			
			constructor = thisJoinPoint.getThis();
//			System.out.println("mc-this: "+constructor);
			// Key: <method_name>+<method_param>+<constructor>
			key += constructor.toString();
		}
		
		// If the method has already been collected, skip it;
		if (methodCollector.containsKey(key)) { return; }
		
		// Checks if it is an internal call (if it is, ignore it)
		if (isInternalCall(signature, testMethodSignature)) { return; }		
		
		// Gets class path
		try {
			classPath = CollectorExecutionFlow.findCurrentClassPath();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		// Gets method signature
		String methodSignature = CollectorExecutionFlow.extractMethodSignature(signature);
		
		// Collects the method
		ClassMethodInfo cmi = new ClassMethodInfo.ClassMethodInfoBuilder()
				.classPath(classPath)
				.methodSignature(methodSignature)
				.testMethodSignature(testMethodSignature)
				.methodName(methodName)
				.returnType(returnType)
				.parameterTypes(paramTypes)
				.args(thisJoinPoint.getArgs())
				.build();

		CollectorInfo ci = new CollectorInfo(cmi);
		
		// Collects constructor (if method is not static)
		if (constructor != null) {
			ci.setConstructorInfo(consCollector.get(constructor.toString()));
		}
		
		// Stores collected method
		methodCollector.put(key, ci);
		lastInsertedMethod = signature;
		
		//###################################################
//		System.out.println("methodCollector.put($$"+key+"$$,"+cmi+")");
		//###################################################
	}
	
	//-----------------------------------------------------------------------
	//		Methods
	//-----------------------------------------------------------------------
	/**
	 * Checks if a signature belong to an internal call.
	 * 
	 * @param signature Signature of the method
	 * @param testMethodSignature Signature of the test method
	 * @return If it belong to an internal call
	 */
	private boolean isInternalCall(String signature, String testMethodSignature)
	{
		// Removes parentheses from the signature of the test method
		testMethodSignature = testMethodSignature.replaceAll("\\(\\)", "");
		
		// It is necessary because if it is an internal call, the next will also be
		if (lastWasInternalCall) {
			lastWasInternalCall = false;
			return true;
		}
		
		// Checks the execution stack to see if it is an internal call
		if (!Thread.currentThread().getStackTrace()[4].toString().contains(testMethodSignature)) {
			lastWasInternalCall = true;
			return true;
		}
		
		return false;
	}
	
	/**
	 * Extracts class name from a method signature.
	 * 
	 * @param signature Method signature
	 * @return Class name
	 */
	private String getClassName(String signature)
	{
		String response;
		String[] tmp = signature.split("\\.");
		
		if (tmp.length < 2)
			response = tmp[0];
		else
			response = tmp[tmp.length-2];
		
		return response;	
	}
	
	/**
	 * Extracts return type of a method.
	 * 
	 * @param jp JoinPoint with the method
	 * @return Class of return type of the method
	 */
	private Class<?> extractReturnType(JoinPoint jp)
	{
		Method method = ((MethodSignature) jp.getSignature()).getMethod();
		return method.getReturnType();
	}
	
	/**
	 * Extracts parameter types of a method.
	 * 
	 * @param jp JoinPoint with the method
	 * @return Classes of parameter types of the method
	 */
	private Class<?>[] extractParameterTypes(JoinPoint jp)
	{
		Method method = ((MethodSignature) jp.getSignature()).getMethod();
		return method.getParameterTypes();
	}
}
