package wniemiec.executionflow.io.processing.manager;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import wniemiec.executionflow.App;
import wniemiec.executionflow.collector.ConstructorCollector;
import wniemiec.executionflow.collector.MethodCollector;
import wniemiec.executionflow.invoked.TestedInvoked;
import wniemiec.executionflow.io.processing.file.InvokedFileProcessor;
import wniemiec.executionflow.io.processing.file.TestMethodFileProcessor;

public class CollectorProcessingManager {

	private static CollectorProcessingManager instance;
	private Set<String> alreadyChanged;
	
	
	private CollectorProcessingManager() {
		alreadyChanged = new HashSet<>();
		
	}
	
	public static CollectorProcessingManager getInstance() {
		if (instance == null)
			instance = new CollectorProcessingManager();
		
		return instance;
	}
	
	
	public void refreshInvocationLineAfterInvokedProcessing(TestedInvoked collector) {
		if (App.isTestMode()) {
			if (collector.getTestedInvoked().getSrcPath().equals(
					collector.getTestMethod().getSrcPath())) {
				updateCollector(collector, InvokedFileProcessor.getMapping());
			}
		}
		else {
			updateCollectors(
					InvokedFileProcessor.getMapping(),
					collector.getTestMethod().getSrcPath(), 
					collector.getTestedInvoked().getSrcPath()
			);
		}
	}

	private void updateCollector(TestedInvoked collector, Map<Integer, Integer> mapping) {
		int invocationLine = collector.getTestedInvoked().getInvocationLine();
		
		if (mapping.containsKey(invocationLine))
			collector.getTestedInvoked().setInvocationLine(mapping.get(invocationLine));
	}

	private void updateCollectors(Map<Integer, Integer> mapping, Path testMethodSrcPath,
								  Path invokedSrcPath) {
		if (alreadyChanged.contains(testMethodSrcPath.toString()) && 
				!invokedSrcPath.equals(testMethodSrcPath))
			return;

		ConstructorCollector.updateInvocationLines(
				mapping, 
				testMethodSrcPath
		);
		
		MethodCollector.updateInvocationLines(
				mapping, 
				testMethodSrcPath
		);
		
		alreadyChanged.add(testMethodSrcPath.toString());
	}

	public void refreshInvocationLineAfterTestMethodProcessing(TestedInvoked collector) {
		if (App.isTestMode()) {
			updateCollector(collector, TestMethodFileProcessor.getMapping());
		}
		else {
			updateCollectors(
					TestMethodFileProcessor.getMapping(),
					collector.getTestMethod().getSrcPath(), 
					collector.getTestedInvoked().getSrcPath()
			);
		}
	}
	
	public void reset() {
		alreadyChanged.clear();
	}
}
