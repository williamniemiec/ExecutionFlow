package wniemiec.app.executionflow.io.processing.manager;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import wniemiec.app.executionflow.collector.InvokedCollector;

/**
 * Responsible for managing processing in collected methods and constructors.
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @since		7.0.0
 */
public class CollectorProcessingManager {

	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	private static CollectorProcessingManager instance;
	private Set<String> alreadyChanged;
	private Set<InvokedCollector> collectors;
	
	
	//-------------------------------------------------------------------------
	//		Constructors
	//-------------------------------------------------------------------------
	private CollectorProcessingManager(Set<InvokedCollector> collectors) {
		alreadyChanged = new HashSet<>();
		this.collectors = collectors;
	}
	
	
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	public static CollectorProcessingManager getInstance(Set<InvokedCollector> collectors) {
		if ((collectors == null) || collectors.isEmpty())
			throw new IllegalArgumentException("Collectors cannot be empty");
		
		if (instance == null)
			instance = new CollectorProcessingManager(collectors);
		
		return instance;
	}
	
	/**
	 * Updates invocation line of all collected collectors based on a 
	 * processing performed in a test method.
	 * 
	 * @param		mapping Mapping of the old lines in relation to the new 
	 * lines of the processed test method 
	 * @param		testMethodSrcPath Test method that has been processed
	 * @param		invokedSrcPath Tested invoked
	 */
	public void updateCollectorsFromMapping(Map<Integer, Integer> mapping, 
								 			Path testMethodSrcPath,
								 			Path invokedSrcPath) {
		if (mapping == null)
			throw new IllegalArgumentException("Mapping cannot be null");
		
		if (testMethodSrcPath == null)
			throw new IllegalArgumentException("Test method source path " + 
											   "cannot be null");
		
		if (invokedSrcPath == null)
			throw new IllegalArgumentException("Invoked source path cannot " + 
											   "be null");
		
		if (alreadyChanged.contains(testMethodSrcPath.toString()) && 
				!invokedSrcPath.equals(testMethodSrcPath))
			return;
		
		for (InvokedCollector collector : collectors) {
			collector.updateInvocationLines(
					mapping, 
					testMethodSrcPath
			);
		}
		
		alreadyChanged.add(testMethodSrcPath.toString());
	}
	
	/**
	 * Removes already processed collectors
	 */
	public void reset() {
		alreadyChanged.clear();
	}
}
