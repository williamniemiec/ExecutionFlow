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

import wniemiec.executionflow.invoked.Invoked;
import wniemiec.util.logger.Logger;

public class CallCollector {
	
	private static CallCollector instance;
	private Map<Invoked, Set<String>> methodsCalledByTestedInvoked;
	private static final File MCTI_FILE;
	
	static {
		File tmpDir = new File(System.getProperty("java.io.tmpdir"));
		
		MCTI_FILE = new File(tmpDir, "mcti.ef");
	}
	
	private CallCollector() {
		methodsCalledByTestedInvoked = new HashMap<>();	
	}
	
	public static CallCollector getInstance() {
		if (instance == null)
			instance = new CallCollector();
		
		return instance;
	}
	
	public void collectCall(String signatureOfMethodCalledByInvoked, Invoked invoked) {
		if (methodsCalledByTestedInvoked.containsKey(invoked)) {
			Set<String> invokedMethods = methodsCalledByTestedInvoked.get(invoked);
			invokedMethods.add(signatureOfMethodCalledByInvoked);
		}
		else {
			Set<String> invokedMethods = new HashSet<>();
			invokedMethods.add(signatureOfMethodCalledByInvoked);
			
			methodsCalledByTestedInvoked.put(invoked, invokedMethods);
		}
		
		storeCall();
	}
	
	private void storeCall() {
		if (methodsCalledByTestedInvoked.isEmpty())
			return;
		
		store(methodsCalledByTestedInvoked);
	}
	
	
	private void store(Map<Invoked, Set<String>> methodsCalledByTestedInvoked) {
		this.methodsCalledByTestedInvoked = methodsCalledByTestedInvoked;
		storeCollectedMethods();
	}
	
	/**
	 * Saves methods called by tested invoked. It will save to a file named
	 * 'mcti.ef' (Methods Called by Tested Invoked).
	 */
	private void storeCollectedMethods() {
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
	private void load() throws FileNotFoundException, IOException {
		if (!MCTI_FILE.exists())
			return;
	
		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(MCTI_FILE))) {
			if (ois.available() == 0)
				return;
			
			@SuppressWarnings("unchecked")
			Map<Invoked, Set<String>> storedCollection = 
					(Map<Invoked, Set<String>>) ois.readObject();
			
			combineCollectedMethodWithStoredCollection(storedCollection);
		}
		catch(java.io.EOFException | ClassNotFoundException e) {
			MCTI_FILE.delete();
		}
	}
	
	private void combineCollectedMethodWithStoredCollection(Map<Invoked, Set<String>> 
															storedCollection) {
		for (Map.Entry<Invoked, Set<String>> e : storedCollection.entrySet()) {
			Invoked storedInvocation = e.getKey();
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
	
	private void mergeCollectedMethodWithStoredCollection(Invoked storedInvocation, 
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
	
	public void mergeMethodsCalledByTestedInvoked() {
		Map<Invoked, Set<String>> invokedMethods = loadMethodsCalledByTestedInvoked();
		
		for (Map.Entry<Invoked, Set<String>> mcti : invokedMethods.entrySet()) {
			if (methodsCalledByTestedInvoked.containsKey(mcti.getKey())) {
				methodsCalledByTestedInvoked.get(mcti.getKey()).addAll(mcti.getValue());
			}
			else {
				methodsCalledByTestedInvoked.put(mcti.getKey(), mcti.getValue());
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	private Map<Invoked, Set<String>> loadMethodsCalledByTestedInvoked() {
		if (!MCTI_FILE.exists())
			return new HashMap<>();
		
		Map<Invoked, Set<String>> invokedMethods = new HashMap<>();

		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(MCTI_FILE))) {
			System.out.println(ois.available());
			invokedMethods = (Map<Invoked, Set<String>>) ois.readObject();
		} 
		catch (IOException | ClassNotFoundException e) {
			Logger.error("Methods called by tested invoked - " + e.getMessage());
		}
	
		MCTI_FILE.delete();
		
		return invokedMethods;
	}
	
	/**
	 * Stores {@link #methodsCalledByTestedInvoked} in the 'mcti.ef' file.
	 * 
	 * @throws		FileNotFoundException If 'mcti.ef' does not exist, is a 
	 * directory rather than a regular file, or for some other reason cannot be
	 * opened for reading.
	 * @throws		IOException If 'mcti.ef' cannot be written
	 */
	private void store() throws FileNotFoundException, IOException {
		try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(MCTI_FILE))) {
			System.out.println(methodsCalledByTestedInvoked);
			oos.writeObject(methodsCalledByTestedInvoked);
			oos.flush();
		}
	}
	
	public void reset() {
		methodsCalledByTestedInvoked.clear();
	}
	
	public boolean deleteStoredContent() {
		return MCTI_FILE.delete();
	}
	
	public Map<Invoked, Set<String>> getMethodsCalledByTestedInvoked() {
		try {
			load();
		} 
		catch (IOException e) {
			// If an exception occurs, returns the current collection
		}
		
		return methodsCalledByTestedInvoked;
	}
}
