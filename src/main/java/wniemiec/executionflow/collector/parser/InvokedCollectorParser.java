package wniemiec.executionflow.collector.parser;

import java.io.IOException;
import java.nio.channels.InterruptedByTimeoutException;
import java.util.Collection;
import java.util.Set;

import wniemiec.executionflow.analyzer.DebuggerAnalyzer;
import wniemiec.executionflow.analyzer.DebuggerAnalyzerFactory;
import wniemiec.executionflow.collector.InvokedCollector;
import wniemiec.executionflow.invoked.TestedInvoked;
import wniemiec.executionflow.io.processing.manager.ProcessingManager;
import wniemiec.util.logger.Logger;

public class InvokedCollectorParser {
	
	private ProcessingManager processingManager;

	public InvokedCollectorParser() {
		processingManager = ProcessingManager.getInstance();
	}
	
//	public static TestedInvokedParser parseMethodCollector(Set<TestedInvoked> methodCollector) {
//		return parseInvokedCollector(methodCollector, false);
//	}
//
//	public static TestedInvokedParser parseConstructorCollector(Set<TestedInvoked> constructorCollector) {
//		return parseInvokedCollector(constructorCollector, true);
//	}
	
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
				InvokedCollectorParser.class, 
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
