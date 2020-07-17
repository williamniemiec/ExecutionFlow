package executionFlow.runtime.collector;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import executionFlow.ExecutionFlow;


/**
 * Captures all methods called within the tested invoked, where an invoked can
 * be a method or a constructor.
 * 
 * @apiNote		Test method, that is, the method that calls the tested method
 * must have {@link executionFlow.runtime._SkipInvoked} annotation
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		2.0.0
 * @since		2.0.0
 */
public aspect InvokedMethodsCollector extends RuntimeCollector
{
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	private String invocationSignature;
	private Map<String, List<String>> methodsCalledByTestedInvoked = new HashMap<>();
	
	
	//-----------------------------------------------------------------------
	//		Pointcuts
	//-----------------------------------------------------------------------
	/**
	 * Gets tested method signatures by a JUnit test that has 
	 * {@link @executionFlow.runtime._SkipInvoked} annotation.
	 */
	pointcut invokedSignature(): 
		!within(@executionFlow.runtime.SkipCollection *) &&
		!withincode(@executionFlow.runtime.SkipInvoked * *.*(..)) &&
		cflow(execution(@executionFlow.runtime._SkipInvoked * *.*(..))) && 
		(junit4() || junit5()) &&
		!junit4_internal() && !junit5_internal() &&
		!execution(public int hashCode());
	
	before(): invokedSignature()
	{
		String signature = thisJoinPoint.getSignature().toString();
		

		// Ignores native java methods
		if (isNativeMethod(signature)) { return; }

		if (signature.indexOf("(") == -1) { return; }
		
		// Gets correct signature of inner classes
		invocationSignature = thisJoinPoint.getSignature().getDeclaringTypeName() + "." 
				+ thisJoinPoint.getSignature().getName() + signature.substring(signature.indexOf("("));
		
		// Stores current invoker signature without its return type
		invocationSignature = CollectorExecutionFlow.extractMethodSignature(signature);
	}
	
	/**
	 * Intercepts methods called within an invoked with 
	 * {@link @executionFlow.runtime.CollectCalls} annotation.
	 */
	pointcut invokedMethodsByTestedInvoker():
		!withincode(@executionFlow.runtime.SkipInvoked * *.*(..)) &&
		// Within a constructor
		( withincode(@executionFlow.runtime.CollectCalls *.new(..)) && 
		  !cflowbelow(withincode(@executionFlow.runtime.CollectCalls * *(..))) ) ||
		// Within a method
		( withincode(@executionFlow.runtime.CollectCalls * *(..)) && 
		  !cflowbelow(withincode(@executionFlow.runtime.CollectCalls *.new(..))) && 
		  !cflowbelow(withincode(@executionFlow.runtime.CollectCalls * *(..))) );
	
	before(): invokedMethodsByTestedInvoker()
	{
		String methodCalledSignature = thisJoinPoint.getSignature().toString();
		
		
		// Checks if is a method signature
		if (!isMethodSignature(methodCalledSignature)) { return; }
		
		// Ignores native java methods
		if (isNativeMethod(methodCalledSignature)) { return; }

		if (invocationSignature == null) { return; }
		
		// Removes return type from the signature of the method called
		methodCalledSignature = thisJoinPoint.getSignature().getDeclaringTypeName() + "." 
				+ thisJoinPoint.getSignature().getName() + methodCalledSignature.substring(methodCalledSignature.indexOf("("));
		
		// Stores current signature of the method called without its return type
		methodCalledSignature = CollectorExecutionFlow.extractMethodSignature(methodCalledSignature);
		
		// Stores method called in methodsCalledByTestedInvoked
		if (!methodsCalledByTestedInvoked.containsKey(invocationSignature)) {
			List<String> invokedMethods = new ArrayList<>();
			
			
			invokedMethods.add(methodCalledSignature);
			methodsCalledByTestedInvoked.put(invocationSignature, invokedMethods);
		}
		else {
			List<String> invokedMethods = methodsCalledByTestedInvoked.get(invocationSignature);
			
			
			invokedMethods.add(methodCalledSignature);
		}
	}
	
	/**
	 * Saves methods called by tested invoked. It will save to a file named
	 * 'mcti.ef' (Methods Called by Tested Invoker).
	 */
	pointcut writer():
		!withincode(@executionFlow.runtime.SkipInvoked * *.*(..)) &&
		execution(@executionFlow.runtime._SkipInvoked * *.*(..)) && 
		!withincode(@executionFlow.runtime._SkipInvoked * *.*(..));
	
	after() returning(): writer() {
		File f = new File(ExecutionFlow.getAppRootPath(), "mcti.ef");
		
		if (!methodsCalledByTestedInvoked.isEmpty()) {
			if (f.exists()) {
				// Reads file (if exists)
				try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) {
					@SuppressWarnings("unchecked")
					Map<String, List<String>> map = (Map<String, List<String>>) ois.readObject();
					
					
					for (Map.Entry<String, List<String>> e : map.entrySet()) {
						// Merges methods called by tested invoked with saved
						// collection
						if (methodsCalledByTestedInvoked.containsKey(e.getKey())) {
							List<String> methodsCalled = methodsCalledByTestedInvoked.get(e.getKey());
							
							for (String invokedMethod : e.getValue())
								methodsCalled.add(invokedMethod);
						}
						// Saves collected methods called by tested invoked
						else {
							methodsCalledByTestedInvoked.put(e.getKey(), e.getValue());							
						}
					}
				} catch (IOException | ClassNotFoundException e) {
					
				}
			}
			
			// Writes file
			try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(f))) {
				oos.writeObject(methodsCalledByTestedInvoked);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		
		methodsCalledByTestedInvoked.clear();
	}
}
