package wniemiec.app.java.executionflow.io.processing.manager;

import java.io.IOException;
import java.nio.channels.InterruptedByTimeoutException;
import java.util.Collection;

import wniemiec.app.java.executionflow.analyzer.DebuggerAnalyzer;
import wniemiec.app.java.executionflow.analyzer.DebuggerAnalyzerFactory;
import wniemiec.app.java.executionflow.collector.InvokedCollector;
import wniemiec.app.java.executionflow.collector.parser.TestedInvokedParser;
import wniemiec.app.java.executionflow.invoked.TestedInvoked;
import wniemiec.io.java.Consolex;

/**
 * Responsible for processing and parsing of tested methods and constructors.
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
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
				Consolex.writeWarning("Time exceeded");
			} 
			catch (IllegalStateException e2) {
				Consolex.writeError(e2.toString());
			}
			catch (Exception e3) {
				Consolex.writeError(e3.toString());
				
				processingManager.undoLastProcessing();
			}
		}
		
		return parser;
	}

	private void doProcessingAndParsing(TestedInvokedParser parser, 
										TestedInvoked collector) 
			throws Exception {
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
		Consolex.writeDebug(
				TestedInvokedProcessingManager.class.getName() +  
				" - collector: " + invokedCollector.toString()
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
