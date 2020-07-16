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
 * Captures all invoked methods within the tested invoker, where an invoker can
 * be a method or a constructor.
 * 
 * @apiNote		Test method, that is, the method that calls the tested method
 * must have {@link executionFlow.runtime._SkipInvoker} annotation
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		1.5
 * @since		1.5
 */
public aspect InvokedMethodsCollector extends RuntimeCollector
{
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	private String invocationSignature;
	private Map<String, List<String>> invokedMethodsByTestedInvoker = new HashMap<>();
	
	
	//-----------------------------------------------------------------------
	//		Pointcuts
	//-----------------------------------------------------------------------
	/**
	 * Gets tested method signatures by a JUnit test that has 
	 * {@link @executionFlow.runtime._SkipMethod} annotation.
	 */
	pointcut invokerSignature(): 
		!within(@SkipCollection *) &&
		cflow(execution(@executionFlow.runtime._SkipInvoker * *.*(..))) && 
		(junit4() || junit5()) &&
		!junit4_internal() && !junit5_internal() &&
		!execution(public int hashCode());
	
	before(): invokerSignature()
	{
		String signature = thisJoinPoint.getSignature().toString();
		

		// Ignores native java methods
		if (isNativeMethod(signature)) { return; }

		if (signature.indexOf("(") == -1) { return; }
		
		System.out.println("s: "+signature);
		System.out.println("idx: "+signature.indexOf("("));
		
		
		// Gets correct signature of inner classes
		invocationSignature = thisJoinPoint.getSignature().getDeclaringTypeName() + "." 
				+ thisJoinPoint.getSignature().getName() + signature.substring(signature.indexOf("("));
		
		// Stores current invoker signature without its return type
		invocationSignature = CollectorExecutionFlow.extractMethodSignature(signature);
	}
	
	/**
	 * Gets invoked method signatures within an invoker with 
	 * {@link @executionFlow.runtime.CollectInvokedMethods} annotation.
	 */
	pointcut invokedMethodsByTestedInvoker():
		// Within a constructor
		( withincode(@executionFlow.runtime.CollectInvokedMethods *.new(..)) && 
		  !cflowbelow(withincode(@executionFlow.runtime.CollectInvokedMethods * *(..))) ) ||
		// Within a method
		( withincode(@executionFlow.runtime.CollectInvokedMethods * *(..)) && 
		  !cflowbelow(withincode(@executionFlow.runtime.CollectInvokedMethods *.new(..))) && 
		  !cflowbelow(withincode(@executionFlow.runtime.CollectInvokedMethods * *(..))) );
	
	before(): invokedMethodsByTestedInvoker()
	{
		String invokedMethodSignature = thisJoinPoint.getSignature().toString();
		
		
		// Checks if is a method signature
		if (!isMethodSignature(invokedMethodSignature)) { return; }
		
		// Ignores native java methods
		if (isNativeMethod(invokedMethodSignature)) { return; }

		if (invocationSignature == null) { return; }
		
		// Removes return type from invoked method signature
		invokedMethodSignature = thisJoinPoint.getSignature().getDeclaringTypeName() + "." 
				+ thisJoinPoint.getSignature().getName() + invokedMethodSignature.substring(invokedMethodSignature.indexOf("("));
		
		// Stores current invoker signature without its return type
		invokedMethodSignature = CollectorExecutionFlow.extractMethodSignature(invokedMethodSignature);
		
		// Stores invoked method in invokedMethodsByTestedInvoker
		if (!invokedMethodsByTestedInvoker.containsKey(invocationSignature)) {
			List<String> invokedMethods = new ArrayList<>();
			
			
			invokedMethods.add(invokedMethodSignature);
			invokedMethodsByTestedInvoker.put(invocationSignature, invokedMethods);
		}
		else {
			List<String> invokedMethods = invokedMethodsByTestedInvoker.get(invocationSignature);
			
			
			invokedMethods.add(invokedMethodSignature);
		}
	}
	
	/**
	 * Saves invoked methods by tested invoker. It will save to a file named
	 * 'imti.ef' (Invoked Methods by Tested Invoker).
	 */
	pointcut writer():
		execution(@executionFlow.runtime._SkipInvoker * *.*(..)) && 
		!withincode(@executionFlow.runtime._SkipInvoker * *.*(..));
	
	after() returning(): writer() {
		File f = new File(ExecutionFlow.getAppRootPath(), "imti.ef");
		
		if (!invokedMethodsByTestedInvoker.isEmpty()) {
			if (f.exists()) {
				// Reads file (if exists)
				try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) {
					@SuppressWarnings("unchecked")
					Map<String, List<String>> map = (Map<String, List<String>>) ois.readObject();
					
					
					for (Map.Entry<String, List<String>> e : map.entrySet()) {
						// Merges collected invoked methods by tested invoker 
						// with saved collection
						if (invokedMethodsByTestedInvoker.containsKey(e.getKey())) {
							List<String> invokedMethods = invokedMethodsByTestedInvoker.get(e.getKey());
							
							for (String invokedMethod : e.getValue())
								invokedMethods.add(invokedMethod);
						}
						// Saves collected invoked methods by tested invoker
						else {
							invokedMethodsByTestedInvoker.put(e.getKey(), e.getValue());							
						}
					}
				} catch (IOException | ClassNotFoundException e) {
					
				}
			}
			
			// Writes file
			try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(f))) {
				oos.writeObject(invokedMethodsByTestedInvoker);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		
		invokedMethodsByTestedInvoker.clear();
	}
}
