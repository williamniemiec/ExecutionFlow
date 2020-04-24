package executionFlow.runtime;

import executionFlow.*;
import executionFlow.core.*;
import executionFlow.exporter.*;
import executionFlow.info.*;
import org.junit.Test;


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
		(execution(@Test * *.*()) || 
		 execution(@RepeatedTest * *.*()) ||
		 execution(@ParameterizedTest * *.*()) ||
		 execution(@TestFactory * *.*())) 
		&& !within(ExecutionFlow)
		&& !within(MethodExecutionFlow)
		&& !within(CheapCoverage)
		&& !within(RT)
		&& !within(MethodDebugger)
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
		&& !within(TestMethodCollector);
	
	/**
	 * Executed before each method with <code>@Test</code> annotation.
	 */
	
	before(): testMethodCollector()
	{
		if (hasSkipCollectionAnnotation(thisJoinPoint)) { return; }
		
		reset();
		
		if (projectPath == "") {
			projectPath = System.getProperty("user.dir");
		}
		
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
		
		System.out.println("@@@"+projectPath);
		// Gets test paths of the collected methods and export them
		ExecutionFlow ef = new ExecutionFlow(methodCollector.values());
		
		try {
			ef.execute().export();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		
		reset();	// Prepares for next test
	}
}
