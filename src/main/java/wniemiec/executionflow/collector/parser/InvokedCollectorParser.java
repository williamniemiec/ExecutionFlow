package wniemiec.executionflow.collector.parser;

import java.io.IOException;
import java.nio.channels.InterruptedByTimeoutException;
import java.util.Set;

import wniemiec.executionflow.analyzer.DebuggerAnalyzer;
import wniemiec.executionflow.analyzer.DebuggerAnalyzerFactory;
import wniemiec.executionflow.collector.InvokedCollector;
import wniemiec.executionflow.invoked.TestedInvoked;
import wniemiec.executionflow.io.processing.manager.ProcessingManager;
import wniemiec.util.logger.Logger;

public class InvokedCollectorParser {

	private InvokedCollectorParser() {
	}
	
	public static TestedInvokedParser parseMethodCollector(Set<TestedInvoked> methodCollector) {
		return parseInvokedCollector(methodCollector, false);
	}

	public static TestedInvokedParser parseConstructorCollector(Set<TestedInvoked> constructorCollector) {
		return parseInvokedCollector(constructorCollector, true);
	}
	
	/**
	 * Runs the application by performing the following tasks: 
	 * <ul>
	 * 	<li>Computes test path</li>
	 * 	<li>Exports test path</li>
	 * 	<li>Exports methods called by tested invoked</li>
	 * 	<li>Exports test methods that test the invoked</li>
	 * 	<li>Exports processed source file</li>
	 * </ul>
	 */
	private static TestedInvokedParser parseInvokedCollector(Set<TestedInvoked> invokedCollector, 
															 boolean isConstructor) {
		if ((invokedCollector == null) || invokedCollector.isEmpty())
			return new TestedInvokedParser();
		
		TestedInvokedParser parser = new TestedInvokedParser();
		ProcessingManager processingManager = ProcessingManager.getInstance();
		
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
	
	private static void dumpCollector(Set<TestedInvoked> invokedCollector) {
		Logger.debug(
				InvokedCollectorParser.class, 
				"collector: " + invokedCollector.toString()
		);
	}
	
	private static boolean isTestedInvokedInTheSameFileAsTestMethod(TestedInvoked collector) {
		return collector.getTestedInvoked().getSrcPath().equals(
				collector.getTestMethod().getSrcPath());
	}
}
