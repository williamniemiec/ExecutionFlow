package executionFlow.runtime;

import java.io.IOException;

import org.junit.Test;

import executionFlow.ExecutionFlow;
import executionFlow.core.JDB;
import executionFlow.core.file.FileCompiler;
import executionFlow.core.file.FileManager;
import executionFlow.core.file.parser.FileParser;
import executionFlow.exporter.ConsoleExporter;
import executionFlow.exporter.FileExporter;
import executionFlow.info.ClassConstructorInfo;
import executionFlow.info.ClassMethodInfo;
import executionFlow.info.CollectorInfo;
import executionFlow.info.SignaturesInfo;


/**
 * Captures all executed methods with <code>@Test</code> annotation, not 
 * including internal calls.
 * 
 * @author William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @since 1.0
 * @version 1.4
 * 
 * @implNote Ignores methods with {@link SkipMethod]} annotation, methods with
 * {@link _SkipMethod] and all methods from classes with {@link SkipCollection}
 * annotation
 */
public aspect TestMethodCollector extends RuntimeCollector
{
	//-------------------------------------------------------------------------
	//		Pointcut
	//-------------------------------------------------------------------------
	pointcut testMethodCollector():
		!cflow(execution(@SkipMethod * *.*())) 
		&& !cflow(execution(@_SkipMethod * *.*()))
		&& execution(@Test * *.*())
		&& !execution(public int hashCode())
//		(execution(@Test * *.*()) || 
		 //execution(@RepeatedTest * *.*()) ||
//		 execution(@ParameterizedTest * *.*()) ||
//		 execution(@TestFactory * *.*())) 
		&& !within(ExecutionFlow)
		&& !within(JDB)
		&& !within(FileCompiler)
		&& !within(FileParser)
		&& !within(FileManager)
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
	 * Executed before each method with <code>@Test</code> annotation.
	 */
	before(): testMethodCollector()
	{
		if (hasSkipCollectionAnnotation(thisJoinPoint)) { return; }
		
		reset();

		testMethodSignature = CollectorExecutionFlow.extractMethodSignature(thisJoinPoint.getSignature().toString());
		testMethodPackage = testMethodSignature.replaceAll("\\(.*\\)", "");
		
		// Gets test class path
		try {
			String className = thisJoinPoint.getTarget().getClass().getSimpleName();
			String classSignature = thisJoinPoint.getSignature().getDeclaringTypeName();
			testClassPath = CollectorExecutionFlow.findClassPath(className, classSignature);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	/**
	 * Executed after the end of a method with @Test annotation.
	 */
	after(): testMethodCollector() 
	{	
		// Ignores if the class has @SkipCollection annotation
		if (hasSkipCollectionAnnotation(thisJoinPoint)) { return; }
		
		// Gets test paths of the collected methods and export them
		int lastLineTestMethod = Thread.currentThread().getStackTrace()[2].getLineNumber();
		ExecutionFlow ef = new ExecutionFlow(methodCollector, lastLineTestMethod);
		
		try {
			ef.execute().export();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		
		reset();	// Prepares for next test
	}
}
