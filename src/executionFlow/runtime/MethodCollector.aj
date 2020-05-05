package executionFlow.runtime;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import executionFlow.ExecutionFlow;
import executionFlow.core.FileCompiler;
import executionFlow.core.FileParser;
import executionFlow.core.JDB;
import executionFlow.exporter.ConsoleExporter;
import executionFlow.exporter.FileExporter;
import executionFlow.info.ClassConstructorInfo;
import executionFlow.info.ClassMethodInfo;
import executionFlow.info.CollectorInfo;
import executionFlow.info.SignaturesInfo;


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
	//		Attributes
	//-----------------------------------------------------------------------
	private String classPath;
	private String srcPath;
	
	
	//-----------------------------------------------------------------------
	//		Pointcut
	//-----------------------------------------------------------------------
	pointcut methodCollector(): 
		(cflow(execution(@Test * *.*())) || 
//		 cflow(execution(@RepeatedTest * *.*())) ||
//		 cflow(execution(@ParameterizedTest * *.*())) || 
//		 cflow(execution(@TestFactory * *.*())) || 
		 cflow(call(* *.*(*))) || 
		 cflow(call(* *.*()))) 
		&& !within(ExecutionFlow)
		&& !within(JDB)
		&& !within(FileCompiler)
		&& !within(FileParser)
		&& !within(ConsoleExporter)
		&& !within(FileExporter)
		&& !within(ClassConstructorInfo)
		&& !within(ClassMethodInfo)
		&& !within(CollectorInfo)
		&& !within(SignaturesInfo)
		&& !within(CollectorExecutionFlow)
		&& !within(ConstructorCollector) 
		&& !within(MethodCollector)
		&& !within(RuntimeCollector)
		&& !within(TestMethodCollector)
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
		String methodName = CollectorExecutionFlow.extractMethodName(signature);
		
		// Extracts types of method parameters (if there is any)
		Class<?>[] paramTypes = CollectorExecutionFlow.extractParamTypes(thisJoinPoint);
		Class<?> returnType = CollectorExecutionFlow.extractReturnType(thisJoinPoint);		
		
		// Key is method's signature + values of method's parameters
		String key = signature+Arrays.toString(thisJoinPoint.getArgs());
		Object constructor = null;
		
		// Checks if there is a constructor (if it is a static method or not)
		if (thisJoinPoint.getThis() != null) {
			constructor = thisJoinPoint.getThis();
			
			// Key: <method_name>+<method_params>+<constructor@hashCode>
			key += constructor.toString();
		}
		
		// If the method has already been collected, skip it;
		if (collectedMethods.contains(key)) { return; }
		
		// Checks if it is an internal call (if it is, ignore it)
		if (isInternalCall(signature)) { return; }		
		
		// Checks if the collected constructor is not the constructor of the test method
		if (constructor != null && isTestMethodConstructor(constructor.toString())) { return; }
		
		// Gets class path
		try {
			classPath = CollectorExecutionFlow.findCurrentClassPath();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		// Gets source path
		try {
			srcPath = CollectorExecutionFlow.findCurrentSrcPath();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// Gets method signature
		String methodSignature = CollectorExecutionFlow.extractMethodSignature(signature);
		
		// Gets method invocation line
		int invocationLine = Thread.currentThread().getStackTrace()[3].getLineNumber();
		
		// Collects the method
		ClassMethodInfo cmi = new ClassMethodInfo.ClassMethodInfoBuilder()
				.classPath(classPath)
				.methodSignature(methodSignature)
				.testMethodSignature(testMethodSignature)
				.methodName(methodName)
				.returnType(returnType)
				.parameterTypes(paramTypes)
				.args(thisJoinPoint.getArgs())
				.invocationLine(invocationLine)
				.srcPath(srcPath)
				.build();

		CollectorInfo ci = new CollectorInfo(cmi);
		
		// Collects constructor (if method is not static)
		if (constructor != null) {
			ci.setConstructorInfo(consCollector.get(constructor.toString()));
		}
		
		// Stores collected method
		collectedMethods.add(key);
		
		// If the method is called in a loop, stores this method in a list with its arguments and constructor
		if (methodCollector.containsKey(invocationLine)) {
			List<CollectorInfo> list = methodCollector.get(invocationLine);
			list.add(ci);
		} else {	// Else stores the method with its arguments and constructor
			List<CollectorInfo> list = new ArrayList<>();
			list.add(ci);
			methodCollector.put(invocationLine, list);
		}
		lastInsertedMethod = signature;
	}
}
