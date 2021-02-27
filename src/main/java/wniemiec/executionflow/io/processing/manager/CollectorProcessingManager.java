package wniemiec.executionflow.io.processing.manager;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import wniemiec.executionflow.collector.InvokedCollector;

public class CollectorProcessingManager {

	private static CollectorProcessingManager instance;
	private Set<String> alreadyChanged;
	private Set<InvokedCollector> collectors;
	
	private CollectorProcessingManager(Set<InvokedCollector> collectors) {
		alreadyChanged = new HashSet<>();
		this.collectors = collectors;
	}
	
	public static CollectorProcessingManager getInstance(Set<InvokedCollector> collectors) {
		if ((collectors == null) || collectors.isEmpty())
			throw new IllegalArgumentException("Collectors cannot be empty");
		
		if (instance == null)
			instance = new CollectorProcessingManager(collectors);
		
		return instance;
	}
	
	
//	public void refreshInvocationLineAfterInvokedProcessing(TestedInvoked collector) {
//		if (App.isTestMode()) {
//			if (collector.getTestedInvoked().getSrcPath().equals(
//					collector.getTestMethod().getSrcPath())) {
//				updateCollector(collector, InvokedFileProcessor.getMapping());
//			}
//		}
//		else {
//			updateCollectors(
//					InvokedFileProcessor.getMapping(),
//					collector.getTestMethod().getSrcPath(), 
//					collector.getTestedInvoked().getSrcPath()
//			);
//		}
//	}

//	private void updateInvocationLineFromMapping(TestedInvoked collector, Map<Integer, Integer> mapping) {
//		int invocationLine = collector.getTestedInvoked().getInvocationLine();
//		
//		if (mapping.containsKey(invocationLine))
//			collector.getTestedInvoked().setInvocationLine(mapping.get(invocationLine));
//	}

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

//		ConstructorCollector.updateInvocationLines(
//				mapping, 
//				testMethodSrcPath
//		);
//		
//		MethodCollector.updateInvocationLines(
//				mapping, 
//				testMethodSrcPath
//		);
		
		alreadyChanged.add(testMethodSrcPath.toString());
	}

//	public void refreshInvocationLineAfterTestMethodProcessing(TestedInvoked collector) {
//		if (App.isTestMode()) {
//			updateCollector(collector, TestMethodFileProcessor.getMapping());
//		}
//		else {
//			updateCollectors(
//					TestMethodFileProcessor.getMapping(),
//					collector.getTestMethod().getSrcPath(), 
//					collector.getTestedInvoked().getSrcPath()
//			);
//		}
//	}
	
	public void reset() {
		alreadyChanged.clear();
	}
}
