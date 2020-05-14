package executionFlow.runtime;

import executionFlow.ExecutionFlow;
import executionFlow.core.*;
import executionFlow.exporter.*;
import executionFlow.info.*;


/**
 * Captures class instantiation.
 */
public aspect ConstructorCollector extends RuntimeCollector
{
	//-----------------------------------------------------------------------
	//		Pointcut
	//-----------------------------------------------------------------------
	pointcut constructorCollector(): 
		!cflow(execution(@SkipMethod * *.*()))
		&& cflow(execution(@Test * *.*()))
		&& (initialization(*.new(*)) || initialization(*.new()))	
		&& !within(ExecutionFlow)
		&& !within(JDB)
		&& !within(FileParser)
		&& !within(FileManager)
		&& !within(FileCompiler)
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
		
		String key = thisJoinPoint.getThis().toString();	
		
		// Checks if it is a constructor signature
		if (!signature.matches(constructorRegex)) { return; }
		
		// Extracts constructor data
		consParamTypes = CollectorExecutionFlow.extractParamTypes(thisJoinPoint.getArgs());
		consParamValues = thisJoinPoint.getArgs();
		
		// Saves extracted data
		consCollector.put(key, new ClassConstructorInfo(consParamTypes, consParamValues));
	}
}
