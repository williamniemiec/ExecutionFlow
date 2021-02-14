package wniemiec.executionflow.collector.parser;

import java.io.IOException;
import java.nio.channels.InterruptedByTimeoutException;
import java.util.Set;

import wniemiec.executionflow.App;
import wniemiec.executionflow.analyzer.DebuggerAnalyzer;
import wniemiec.executionflow.analyzer.DebuggerAnalyzerFactory;
import wniemiec.executionflow.collector.ConstructorCollector;
import wniemiec.executionflow.collector.InvokedCollector;
import wniemiec.executionflow.collector.MethodCollector;
import wniemiec.executionflow.exporter.ExportManager;
import wniemiec.executionflow.invoked.TestedInvoked;
import wniemiec.executionflow.runtime.hook.ProcessingManager;
import wniemiec.util.logger.Logger;

public class InvokedCollectorParser {

	private InvokedCollectorParser() {
	}
	
	public static void parseMethodCollector(Set<TestedInvoked> methodCollector) {
		parseInvokedCollector(methodCollector, false);
	}

	public static void parseConstructorCollector(Set<TestedInvoked> constructorCollector) {
		parseInvokedCollector(constructorCollector, true);
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
	private static void parseInvokedCollector(Set<TestedInvoked> invokedCollector, boolean isConstructor) {
		if ((invokedCollector == null) || invokedCollector.isEmpty())
			return;
		
		TestedInvokedParser parser = new TestedInvokedParser();
		
		dumpCollector(invokedCollector);

		for (TestedInvoked collector : invokedCollector) {				
			try {
				ProcessingManager.doProcessingInTestedInvoked(collector);
				
				DebuggerAnalyzer debuggerAnalyzer = DebuggerAnalyzerFactory.createStandardTestPathAnalyzer(
						collector.getTestedInvoked(), 
						collector.getTestMethod()
				);
				
				parser.parse(collector, debuggerAnalyzer);
				
				if (isTestedInvokedInTheSameFileAsTestMethod(collector)) {
					ProcessingManager.resetLastProcessing();
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
				
				ProcessingManager.resetLastProcessing();
			}
		}
		
		export(parser, isConstructor);
	}
	
	private static void dumpCollector(Set<TestedInvoked> invokedCollector) {
		Logger.debug(
				InvokedCollectorParser.class, 
				"collector: " + invokedCollector.toString()
		);
	}
	
	private static void export(TestedInvokedParser parser, boolean isConstructor) {
		ExportManager exportManager;
		exportManager = new ExportManager(App.isDevelopment(), isConstructor);
		
		exportManager.exportTestPaths(parser.getTestPaths());
		exportManager.exportEffectiveMethodsAndConstructorsUsedInTestMethods(
				parser.getMethodsAndConstructorsUsedInTestMethod()
		);
		exportManager.exportProcessedSourceFiles(parser.getProcessedSourceFiles());
		exportManager.exportMethodsCalledByTestedInvoked(
				parser.getMethodsCalledByTestedInvoked()
		);
	}
	
	private static boolean isTestedInvokedInTheSameFileAsTestMethod(TestedInvoked collector) {
		return collector.getTestedInvoked().getSrcPath().equals(
				collector.getTestMethod().getSrcPath());
	}
}
