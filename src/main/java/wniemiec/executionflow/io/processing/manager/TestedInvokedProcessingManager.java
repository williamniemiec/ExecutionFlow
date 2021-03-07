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

/**
 * Responsible for processing and parsing of tested methods and constructors.
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		7.0.0
 * @since		7.0.0
 */
public class TestedInvokedProcessingManager {

	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	private ProcessingManager processingManager;

	
	//-------------------------------------------------------------------------
	//		Constructor
	//-------------------------------------------------------------------------
	public TestedInvokedProcessingManager() {
		processingManager = ProcessingManager.getInstance();
	}
	
	
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	public TestedInvokedParser processAndParse(Collection<TestedInvoked> invokedCollector) {
		if ((invokedCollector == null) || invokedCollector.isEmpty())
			return new TestedInvokedParser();
		
		TestedInvokedParser parser = new TestedInvokedParser();
		
		processingManager.initializeManagers();
		
		dumpCollector(invokedCollector);

		for (TestedInvoked collector : invokedCollector) {				
			try {
				doProcessingAndParsing(parser, collector);
			}
			catch (InterruptedByTimeoutException e1) {
				Logger.error("Time exceeded");
			} 
			catch (IllegalStateException e2) {
				Logger.error(e2.toString());
			}
			catch (IOException e3) {
				Logger.error(e3.toString());
				
				processingManager.undoLastProcessing();
			}
		}
		
		return parser;
	}

	private void doProcessingAndParsing(TestedInvokedParser parser, 
										TestedInvoked collector) 
			throws IOException {
		processingManager.doProcessingInTestedInvoked(collector);
		
		parser.parse(collector, getDebuggerAnalyzerFor(collector));
		
		if (isTestedInvokedInTheSameFileAsTestMethod(collector)) {
			processingManager.undoLastProcessing();
			InvokedCollector.restoreCollectorInvocationLine();
		}
	}
	
	private DebuggerAnalyzer getDebuggerAnalyzerFor(TestedInvoked testedInvoked) 
			throws IOException {
		return DebuggerAnalyzerFactory.createStandardTestPathAnalyzer(
				testedInvoked
		);
	}
	
	private void dumpCollector(Collection<TestedInvoked> invokedCollector) {
		Logger.debug(
				TestedInvokedProcessingManager.class, 
				"collector: " + invokedCollector.toString()
		);
	}
	
	private boolean isTestedInvokedInTheSameFileAsTestMethod(TestedInvoked collector) {
		return	collector.getTestedInvoked().getSrcPath().equals(
						collector.getTestMethod().getSrcPath()
				);
	}
	
	public void restoreAll() {
		processingManager.restoreOriginalFilesFromInvoked();
		processingManager.restoreOriginalFilesFromTestMethod();
	}
}
