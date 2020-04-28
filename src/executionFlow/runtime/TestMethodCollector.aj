package executionFlow.runtime;

import java.util.Arrays;

import org.junit.Test;

import executionFlow.ExecutionFlow;
import executionFlow.core.*;
import executionFlow.exporter.ConsoleExporter;
import executionFlow.exporter.FileExporter;
import executionFlow.info.ClassConstructorInfo;
import executionFlow.info.ClassMethodInfo;
import executionFlow.info.CollectorInfo;
import executionFlow.info.SignaturesInfo;
import junit.extensions.RepeatedTest;


/**
 * Captures all executed methods with <code>@Test</code> annotation, not including
 * internal calls.
 */
public aspect TestMethodCollector extends RuntimeCollector
{
	//-----------------------------------------------------------------------
	//		Pointcut
	//-----------------------------------------------------------------------
	pointcut testMethodCollector():
		execution(@Test * *.*())
//		(execution(@Test * *.*()) || 
		 //execution(@RepeatedTest * *.*()) ||
//		 execution(@ParameterizedTest * *.*()) ||
//		 execution(@TestFactory * *.*())) 
		&& !within(ExecutionFlow)
		&& !within(CheapCoverage)
		&& !within(RT)
		&& !within(JDB)
		&& !within(TestPathManager)
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
		
		testMethodSignature = thisJoinPoint.getSignature().toString();
		testMethodSignature = testMethodSignature.substring(5);		// Removes return type
	}
	
	/**
	 * Executed after the end of a method with @Test annotation.
	 */
	after(): testMethodCollector() 
	{	
		// Ignores if the class has @SkipCollection annotation
		if (hasSkipCollectionAnnotation(thisJoinPoint)) { return; }
		
		// Gets test paths of the collected methods and export them
		//ExecutionFlow ef = new ExecutionFlow(methodCollector.values());
		//System.out.println("L:"+ thisJoinPoint.getSourceLocation().getLine());
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
