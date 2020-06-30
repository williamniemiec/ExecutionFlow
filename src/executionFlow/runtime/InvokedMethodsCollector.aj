package executionFlow.runtime;

import java.util.ArrayList;
import java.util.List;

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
		
		invocationSignature = signature;
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
		System.out.println("##");
		
		/*File f = new File(ExecutionFlow.getAppRootPath(), "imti.ef");
		
		if (!f.exists()) {
			try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(f))) {
				oos.writeObject(invokedMethodsByTestedInvoker);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}*/
//		else {
//			try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) {
//				ois.readObject();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
			
			
//			
//		System.out.println("$$:"+invokedMethodsByTestedInvoker);
//		invokedMethodsByTestedInvoker.clear();
	}
}
