package wniemiec.executionflow.io.processing.manager;

import java.io.IOException;
import java.nio.channels.InterruptedByTimeoutException;
import java.util.Collection;

import wniemiec.executionflow.analyzer.DebuggerAnalyzer;
import wniemiec.executionflow.analyzer.DebuggerAnalyzerFactory;
import wniemiec.executionflow.collector.InvokedCollector;
import wniemiec.executionflow.collector.parser.TestedInvokedParser;
import wniemiec.executionflow.invoked.TestedInvoked;
import wniemiec.util.logger.Logger;

public class TestedInvokedProcessingManager {

	private ProcessingManager processingManager;

	public TestedInvokedProcessingManager() {
		processingManager = ProcessingManager.getInstance();
	}
	
	public TestedInvokedParser parse(Collection<TestedInvoked> invokedCollector) {
		if ((invokedCollector == null) || invokedCollector.isEmpty())
			return new TestedInvokedParser();
		
		TestedInvokedParser parser = new TestedInvokedParser();
		processingManager.initializeManagers();
		
		dumpCollector(invokedCollector);

		for (TestedInvoked collector : invokedCollector) {				
			try {
				processingManager.doProcessingInTestedInvoked(collector);
				
				DebuggerAnalyzer debuggerAnalyzer = DebuggerAnalyzerFactory.createStandardTestPathAnalyzer(
						collector.getTestedInvoked(), 
						collector.getTestMethod()
				);
				
				parser.parse(collector, debuggerAnalyzer);
				
				if (isTestedInvokedInTheSameFileAsTestMethod(collector)) {
					processingManager.resetLastProcessing();
					InvokedCollector.restoreCollectorInvocationLine();
				}
			}
			catch (InterruptedByTimeoutException e1) {
				Logger.error("Time exceeded");
			} 
			catch (IllegalStateException e2) {
				Logger.error(e2.getMessage());
			}
			catch (IOException e3) {
				Logger.error(e3.getMessage());
				
				processingManager.resetLastProcessing();
			}
		}
		
		return parser;
	}
	
	private void dumpCollector(Collection<TestedInvoked> invokedCollector) {
		Logger.debug(
				TestedInvokedProcessingManager.class, 
				"collector: " + invokedCollector.toString()
		);
	}
	
	private boolean isTestedInvokedInTheSameFileAsTestMethod(TestedInvoked collector) {
		return collector.getTestedInvoked().getSrcPath().equals(
				collector.getTestMethod().getSrcPath());
	}
	
	public void restoreAll() {
		processingManager.restoreOriginalFilesFromInvoked();
		processingManager.restoreOriginalFilesFromTestMethod();
	}
}
