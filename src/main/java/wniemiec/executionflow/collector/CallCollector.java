package wniemiec.executionflow.collector;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import wniemiec.executionflow.App;
import wniemiec.executionflow.invoked.InvokedInfo;
import wniemiec.util.logger.Logger;

public class CallCollector {
	
	private static Map<InvokedInfo, Set<String>> methodsCalledByTestedInvoked;
	
	static {
		methodsCalledByTestedInvoked = new HashMap<>();
	}
	
	public static void collectCall(String signature, InvokedInfo invoked) {
		if (methodsCalledByTestedInvoked.containsKey(invoked)) {
			Set<String> invokedMethods = methodsCalledByTestedInvoked.get(invoked);
			invokedMethods.add(signature);
		}
		else {
			Set<String> invokedMethods = new HashSet<>();
			invokedMethods.add(signature);
			
			methodsCalledByTestedInvoked.put(invoked, invokedMethods);
		}
	}
	
	public static void storeCall() {
		if (methodsCalledByTestedInvoked.isEmpty())
			return;
		
		CallCollector.store(methodsCalledByTestedInvoked);
	}
	
	
	public static void store(Map<InvokedInfo, Set<String>> methodsCalledByTestedInvoked) {
		CallCollector.methodsCalledByTestedInvoked = methodsCalledByTestedInvoked;
	}
	
	/**
	 * Saves methods called by tested invoked. It will save to a file named
	 * 'mcti.ef' (Methods Called by Tested Invoked).
	 */
	private static void storeCollectedMethods() {
		try {
			load();
			store();
		} 
		catch (IOException e) {
			Logger.error("MethodCallsCollector.aj - " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	/**
	 * Loads methods called by tested invoked and merges with attribute 
	 * {@link #methodsCalledByTestedInvoked}.
	 * 
	 * @throws		FileNotFoundException If 'mcti.ef' does not exist, is a 
	 * directory rather than a regular file, or for some other reason cannot be
	 * opened for reading.
	 * @throws		IOException If 'mcti.ef' cannot be read
	 */
	private static void load() throws FileNotFoundException, IOException {
		File file = new File(App.getAppRootPath().toFile(), "mcti.ef");
		
		if (!file.exists())
			return;

		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
			@SuppressWarnings("unchecked")
			Map<InvokedInfo, Set<String>> storedCollection = 
					(Map<InvokedInfo, Set<String>>) ois.readObject();
			
			combineCollectedMethodWithStoredCollection(storedCollection);
		}
		catch(java.io.EOFException | ClassNotFoundException e) {
			file.delete();
		}
	}
	
	private static void combineCollectedMethodWithStoredCollection(Map<InvokedInfo, Set<String>> 
															storedCollection) {
		for (Map.Entry<InvokedInfo, Set<String>> e : storedCollection.entrySet()) {
			InvokedInfo storedInvocation = e.getKey();
			Set<String> storedMethodsCalled = e.getValue();
			
			if (methodsCalledByTestedInvoked.containsKey(storedInvocation)) {
				mergeCollectedMethodWithStoredCollection(
						storedInvocation, 
						storedMethodsCalled
				);
			}
			else {
				methodsCalledByTestedInvoked.put(
						storedInvocation, 
						storedMethodsCalled
				);							
			}
		}
	}
	
	private static void mergeCollectedMethodWithStoredCollection(InvokedInfo storedInvocation, 
														  Set<String> storedMethodsCalled) {
		Set<String> currentMethodsCalled = 
				methodsCalledByTestedInvoked.get(storedInvocation);

		for (String invokedMethod : storedMethodsCalled)
			currentMethodsCalled.add(invokedMethod);
		
		for (String methodCalled : storedMethodsCalled) {
			if (!currentMethodsCalled.contains(methodCalled)) {
				currentMethodsCalled.add(methodCalled);
			}
		}
		
		methodsCalledByTestedInvoked.put(storedInvocation, currentMethodsCalled);
	}
	
	/**
	 * Stores {@link #methodsCalledByTestedInvoked} in the 'mcti.ef' file.
	 * 
	 * @throws		FileNotFoundException If 'mcti.ef' does not exist, is a 
	 * directory rather than a regular file, or for some other reason cannot be
	 * opened for reading.
	 * @throws		IOException If 'mcti.ef' cannot be written
	 */
	private static void store() throws FileNotFoundException, IOException {
		File file = new File(App.getAppRootPath().toFile(), "mcti.ef");
		
		try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
			oos.writeObject(methodsCalledByTestedInvoked);
		}
	}
	
	public static void reset() {
		methodsCalledByTestedInvoked.clear();
	}
}
