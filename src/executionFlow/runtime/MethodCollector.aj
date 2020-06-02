package executionFlow.runtime;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.*;

import executionFlow.*;
import executionFlow.core.*;
import executionFlow.core.file.*;
import executionFlow.core.file.parser.*;
import executionFlow.core.file.parser.factory.*;
import executionFlow.exporter.*;
import executionFlow.info.*;


/**
 * Captures all executed methods with <code>@Test</code> annotation, including
 * inner methods (captures the method and all internal calls to other methods).
 * 
 * @apiNote	Excludes calls to native java methods, ExecutionFlow's classes,
 * methods with {@link SkipMethod]} annotation, methods with {@link _SkipMethod]
 * and all methods from classes with {@link SkipCollection} annotation.
 * 
 * @author	William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version	1.5
 * @since	1.0 
 */
public aspect MethodCollector extends RuntimeCollector
{	
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	private String classPath;
	private String srcPath;
	
	
	//-----------------------------------------------------------------------
	//		Pointcut
	//-----------------------------------------------------------------------
	pointcut methodCollector(): 
		!cflow(execution(@SkipMethod * *.*())) 
		&& !cflow(execution(@_SkipMethod * *.*()))
		&& cflow(execution(@Test * *.*())) 
		&& !execution(public int hashCode())
//		(cflow(execution(@Test * *.*())) || 
//		 cflow(execution(@RepeatedTest * *.*())) ||
//		 cflow(execution(@ParameterizedTest * *.*())) || 
//		 cflow(execution(@TestFactory * *.*()))
		&& !within(is(EnumType))
		&& !within(is(InnerType))
		&& !within(is(AnonymousType))
		&& !within(is(InterfaceType))
		&& !execution(private * *(..)) && !execution(private * *(*)) 
		&& !execution(@Ignore * *())
		&& !execution(@Before * *())
		&& !execution(@After * *())
		&& !execution(@BeforeClass * *())
		&& !execution(@AfterClass * *())
		&& !within(@SkipCollection *)
		&& !within(ExecutionFlow)
		&& !within(JDB)
		&& !within(FileCompiler)
		&& !within(FileParser)
		&& !within(FileManager)
		&& !within(FileParserFactory)
		&& !within(FileEncoding)
		&& !within(MethodFileParser)
		&& !within(MethodFileParserFactory)
		&& !within(TestMethodFileParser)
		&& !within(TestMethodFileParserFactory)
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
		&& !call(void org.junit.Assert.*(*))
		&& !call(void org.junit.Assert.*(*,*))
		&& !call(void org.junit.Assert.*(*,*,*))
		&& !call(void org.junit.Assert.*(*,*,*,*))
		&& !call(void org.junit.Assert.fail());
	
	/**
	 * Executed before the end of each internal call of a method with @Test annotation
	 */
	after(): methodCollector()
	{	
		// Gets method invocation line
		int invocationLine = thisJoinPoint.getSourceLocation().getLine();
		
		if (invocationLine <= 0) { return; }
		
		String signature = thisJoinPoint.getSignature().toString();

		// Ignores native java methods
		if (isNativeMethod(signature)) { return; }
		
		// Ignores methods in the method test (with @Test) (it will only consider internal calls)
		if (testMethodSignature != null && signature.contains(testMethodSignature)) { return; }
		
		// Checks if is a method signature
		if (!isMethodSignature(signature)) { return; }
		
		// Checks if it is an internal call (if it is, ignore it)
		if (isInternalCall(signature)) { return; }
		
		// Ignores methods caught by 'execution', because they are caught by 'call'
		if (thisJoinPoint.toLongString().contains("execution(")) { return; }
		
		// Extracts the method name
		String methodName = CollectorExecutionFlow.extractMethodName(signature);
		
		// Checks if it is a method that is invoked within test method
		String classSignature = thisJoinPoint.getSignature().getDeclaringTypeName();
		String methodSig = classSignature.replace("$", ".")+"."+methodName;
		
		// If it is not, ignores it
		if (!signature.contains(methodSig)) { return; }
		
		// Extracts types of method parameters (if there is any)
		Class<?>[] paramTypes = CollectorExecutionFlow.extractParamTypes(thisJoinPoint);
		Class<?> returnType = CollectorExecutionFlow.extractReturnType(thisJoinPoint);		
		
		// Key is method's signature + values of method's parameters
		String key = signature+Arrays.toString(thisJoinPoint.getArgs());
		Object constructor = null;
		
		// Checks if there is a constructor (if it is a static method or not)
		if (thisJoinPoint.getTarget() != null) {
			constructor = thisJoinPoint.getTarget();
			
			// Key: <method_name>+<method_params>+<constructor@hashCode>
			key += constructor.getClass().getName()+"@"+Integer.toHexString(constructor.hashCode());
		}
		
		// Checks if the collected constructor is not the constructor of the test method
		if (constructor != null && isTestMethodConstructor(key)) { return; }
		
		// If the method has already been collected, skip it (avoids collect duplicate methods)
		if (collectedMethods.contains(key)) {
			order++;
			return; 
		}
		
		// Gets class path and source path
		String testSrcPath = null;
		try {
			// Class path and source path from method
			String className = CollectorExecutionFlow.getClassName(classSignature);
			classPath = CollectorExecutionFlow.findClassPath(className, classSignature);
			srcPath = CollectorExecutionFlow.findSrcPath(className, classSignature);
			
			// Class path and source path from test method
			String testClassSignature = CollectorExecutionFlow.extractClassSignature(testMethodSignature);
			String testClassName = CollectorExecutionFlow.getClassName(testClassSignature);
			testSrcPath = CollectorExecutionFlow.findSrcPath(testClassName, testClassSignature);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		// Gets method signature
		String methodSignature = CollectorExecutionFlow.extractMethodSignature(signature);
		
		if (lastInvocationLine != invocationLine) {
			order = 0;
		}
		
		// Collects the method
		try {
			ClassMethodInfo cmi = new ClassMethodInfo.ClassMethodInfoBuilder()
				.classPath(classPath)
				.testClassPath(testClassPath)
				.methodSignature(methodSignature)
				.testMethodSignature(testMethodSignature)
				.methodName(methodName)
				.returnType(returnType)
				.parameterTypes(paramTypes)
				.args(thisJoinPoint.getArgs())
				.invocationLine(invocationLine)
				.srcPath(srcPath)
				.testSrcPath(testSrcPath)
				.build();
			
			CollectorInfo ci = new CollectorInfo(cmi, order++);
			lastInvocationLine = invocationLine;
			
			// Collects constructor (if method is not static)
			if (constructor != null) {
				ci.setConstructorInfo(consCollector.get(constructor.toString()));
			}
			
			// Stores key of collected method
			collectedMethods.add(key);
			
			// If the method is called in a loop, stores this method in a list with its arguments and constructor
			if (methodCollector.containsKey(invocationLine)) {
				List<CollectorInfo> list = methodCollector.get(invocationLine);
				list.add(ci);
			} 
			else {	// Else stores the method with its arguments and constructor
				List<CollectorInfo> list = new ArrayList<>();
				list.add(ci);
				methodCollector.put(invocationLine, list);
			}
			lastInsertedMethod = signature;
		} catch(IllegalArgumentException e) {
			System.err.println("[ERROR] MethodCollector - "+e.getMessage()+"\n");
		}
	}
}
