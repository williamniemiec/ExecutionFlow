package executionFlow.runtime;

import executionFlow.ExecutionFlow;
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
	pointcut testMethodCollector(): execution(@Test * *.*()) && !within(TestMethodCollector)
															 && !within(RuntimeCollector)
															 && !within(TestMethodCollector)
															 && !within(MethodCollector)
															 && !within(ConstructorCollector);
	
	/**
	 * Executed before each method with <code>@Test</code> annotation.
	 */
	before(): testMethodCollector()
	{
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
		ExecutionFlow ef = new ExecutionFlow(classPath, methodCollector.values(), cci);
		
		try {
			ef.execute().export();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		
		reset();	// Prepares for next test
	}
}
