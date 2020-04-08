package executionFlow.runtime;

import executionFlow.info.ClassConstructorInfo;
import executionFlow.ClassExecutionFlow;
import executionFlow.ExecutionFlow;
import executionFlow.cheapCoverage.CheapCoverage;
import executionFlow.cheapCoverage.RT;
import executionFlow.exporter.*;


/**
 * Captures class instantiation.
 */
public aspect ConstructorCollector extends RuntimeCollector
{
	//-----------------------------------------------------------------------
	//		Pointcut
	//-----------------------------------------------------------------------
	pointcut constructorCollector(): preinitialization(*.new(*)) 	&& !within(RuntimeCollector)
																	&& !within(TestMethodCollector)
																	&& !within(MethodCollector)
																	&& !within(ConstructorCollector)
																	&& !within(CollectorExecutionFlow) 
																	&& !within(ExecutionFlow) 
																	&& !within(ConsoleExporter)
																	&& !within(FileExporter)
																	&& !within(ClassExecutionFlow)
																	&& !within(RT)
																	&& !within(CheapCoverage)
																	&& !call(* org.junit.runner.JUnitCore.runClasses(*))
																	&& !call(void org.junit.Assert.*(*,*));
	
	/**
	 * Executed after instantiating an object.
	 */
	after(): constructorCollector()
	{
		// Ignores if the class has @SkipCollection annotation
		if (hasSkipCollectionAnnotation(thisJoinPoint)) { return; }
		
		String signature = thisJoinPoint.getSignature().toString();
		String constructorRegex = "[^\\s\\t]([A-z0-9-_$]*\\.)*[A-z0-9-_$]+\\([A-z0-9-_$,\\s]*\\)";
		
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
}
