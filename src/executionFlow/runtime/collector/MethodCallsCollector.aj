package executionFlow.runtime.collector;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import executionFlow.ExecutionFlow;
import executionFlow.util.ConsoleOutput;


/**
 * Captures all methods called within the tested invoked, where an invoked can
 * be a method or a constructor.
 * 
 * @apiNote		Test method, that is, the method that calls the tested method
 * must have {@link executionFlow.runtime._SkipInvoked} annotation
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		5.2.0
 * @since		2.0.0
 */
public aspect MethodCallsCollector extends RuntimeCollector
{
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	private String invocationSignature;
	
	
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
		!get(* *.*) && !set(* *.*) &&
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
		!get(* *.*) && !set(* *.*) &&
		//!junit4_internal() && !junit5_internal() && 
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
		methodCalledSignature = methodCalledSignature.replaceAll("\\$", ".");

		// Stores method called in methodsCalledByTestedInvoked
		if (!methodsCalledByTestedInvoked.containsKey(invocationSignature)) {
			Set<String> invokedMethods = new HashSet<>();
			
			
			invokedMethods.add(methodCalledSignature);
			methodsCalledByTestedInvoked.put(invocationSignature, invokedMethods);
		}
		else {
			Set<String> invokedMethods = methodsCalledByTestedInvoked.get(invocationSignature);
			
			
			invokedMethods.add(methodCalledSignature);
		}

		write();
	}
	
	/**
	 * Saves methods called by tested invoked. It will save to a file named
	 * 'mcti.ef' (Methods Called by Tested Invoked).
	 */
	private void write()
	{
		File f = new File(ExecutionFlow.getAppRootPath().toFile(), "mcti.ef");
		
		
		if (!methodsCalledByTestedInvoked.isEmpty()) {
			if (f.exists()) {
				// Reads file (if exists)
				try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) {
					@SuppressWarnings("unchecked")
					Map<String, Set<String>> map = (Map<String, Set<String>>) ois.readObject();
					
					
					for (Map.Entry<String, Set<String>> e : map.entrySet()) {
						// Merges methods called by tested invoked with saved
						// collection
						if (methodsCalledByTestedInvoked.containsKey(e.getKey())) {
							Set<String> methodsCalled = methodsCalledByTestedInvoked.get(e.getKey());
							
							
							for (String invokedMethod : e.getValue())
								methodsCalled.add(invokedMethod);
							
							for (String methodCalled : e.getValue()) {
								if (!methodsCalled.contains(methodCalled)) {
									methodsCalled.add(methodCalled);
								}
							}
							
							methodsCalledByTestedInvoked.put(e.getKey(), methodsCalled);		
						}
						// Saves collected methods called by tested invoked
						else {
							methodsCalledByTestedInvoked.put(e.getKey(), e.getValue());							
						}
					}
				}
				catch(java.io.EOFException e) {
					f.delete();
				}
				catch (IOException | ClassNotFoundException e) {
					ConsoleOutput.showError("MethodCallsCollector.aj - "+e.getMessage());
					e.printStackTrace();
				}
			}
			
			// Writes file
			try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(f))) {
				oos.writeObject(methodsCalledByTestedInvoked);
			} 
			catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
