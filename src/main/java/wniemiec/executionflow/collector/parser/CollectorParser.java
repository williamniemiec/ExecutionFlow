package wniemiec.executionflow.collector.parser;

import java.io.IOException;
import java.nio.channels.InterruptedByTimeoutException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import wniemiec.executionflow.App;
import wniemiec.executionflow.analyzer.Analyzer;
import wniemiec.executionflow.analyzer.AnalyzerFactory;
import wniemiec.executionflow.collector.CollectorContainer;
import wniemiec.executionflow.exporter.ExportManager;
import wniemiec.executionflow.invoked.InvokedContainer;
import wniemiec.executionflow.invoked.InvokedInfo;
import wniemiec.executionflow.io.manager.FileManager;
import wniemiec.executionflow.io.processor.factory.InvokedFileProcessorFactory;
import wniemiec.executionflow.io.processor.factory.TestMethodFileProcessorFactory;
import wniemiec.executionflow.io.processor.fileprocessor.InvokedFileProcessor;
import wniemiec.executionflow.io.processor.fileprocessor.TestMethodFileProcessor;
import wniemiec.executionflow.runtime.hook.ProcessingManager;
import wniemiec.util.logger.Logger;

/**
 * For each collected method or constructor, obtain the following information:
 * <ul>
 * 	<li>Test path</li>
 * 	<li>Methods called by this method</li>
 * 	<li>Test methods that call this method</li>
 * </ul>
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		7.0.0
 * @since		1.0
 */
public abstract class CollectorParser {
	
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	/**
	 * Stores computed test paths from a class.<br />
	 * <ul>
	 * 	<li><b>Key:</b> Test method and tested invoked</li>
	 * 	<li><b>Value:</b> List of test paths</li>
	 * </ul>
	 */
	protected Map<InvokedContainer, List<List<Integer>>> computedTestPaths;
	
	
//	private InvokedManager processingManager;
	private Analyzer analyzer;
	private Map<String, Path> processedSourceFiles;
	private ExportManager exportManager;
	private Set<String> alreadyChanged;
	private Set<InvokedContainer> invokedCollector;
	
	
	
	
	
	
	//-------------------------------------------------------------------------
	//		Constructor
	//-------------------------------------------------------------------------	
	protected CollectorParser(Set<InvokedContainer> invokedCollector) {		
//		this.processingManager = processingManager;
		this.exportManager = new ExportManager(App.isDevelopment(), isConstructor());
		this.computedTestPaths = new HashMap<>();
		this.processedSourceFiles = new HashMap<>();
		this.alreadyChanged = new HashSet<>();
		this.invokedCollector = invokedCollector;
	}
	

	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	/**
	 * Runs the application by performing the following tasks: 
	 * <ul>
	 * 	<li>Computes test path</li>
	 * 	<li>Exports test path</li>
	 * 	<li>Exports methods called by tested invoked</li>
	 * 	<li>Exports test methods that test the invoked</li>
	 * 	<li>Exports processed source file</li>
	 * </ul>
	 * 
	 * @return		Itself to allow chained calls
	 */
	public final CollectorParser run() {
		if ((invokedCollector == null) || invokedCollector.isEmpty())
			return this;
		
		dump();

		for (InvokedContainer collector : invokedCollector) {
			parseCollector(collector);
		}
		
		export();
		
		return this;
	}
	
	private void export() {
		exportManager.exportTestPaths(computedTestPaths);
		exportManager.exportEffectiveMethodsAndConstructorsUsedInTestMethods(
				computedTestPaths.keySet()
		);
		exportManager.exportProcessedSourceFiles(processedSourceFiles);
		exportManager.exportMethodsCalledByTestedInvoked(
				analyzer.getMethodsCalledByTestedInvoked()
		);
	}
	
	private void parseCollector(InvokedContainer collector) {
		FileManager invokedFileManager = createInvokedFileManager(collector);
		FileManager testMethodFileManager = createTestMethodFileManager(collector);
		
		try {
			processTestMethod(collector, testMethodFileManager);
			processInvokedMethod(collector, invokedFileManager, testMethodFileManager);
			
			runDebugger(collector);
			
			storeResults(collector);
			
			if (isTestedInvokedInTheSameFileAsTestMethod(collector)) {
				resetProcessing(invokedFileManager, testMethodFileManager);
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
			
			ProcessingManager.restoreInvokedToBeforeProcessing(invokedFileManager);
			ProcessingManager.restoreTestMethodToBeforeProcessing(testMethodFileManager);
		}
	}

	private void storeResults(InvokedContainer collector) {
		if (!analyzer.hasTestPaths())
			return;
		
		if (isConstructor())
			fixAnonymousClassSignature(collector.getInvokedInfo());
		
		storeTestPath(
				new InvokedContainer(
						collector.getInvokedInfo(), 
						collector.getTestMethodInfo()
		));
		
		processedSourceFiles.put(
				collector.getInvokedInfo().getConcreteInvokedSignature(),
				collector.getInvokedInfo().getSrcPath()
		);
	}
	
	private boolean isTestedInvokedInTheSameFileAsTestMethod(InvokedContainer collector) {
		return collector.getInvokedInfo().getSrcPath().equals(
				collector.getTestMethodInfo().getSrcPath());
	}
	
	private void resetProcessing(FileManager invokedFileManager, 
			 					 FileManager testMethodFileManager) {
		ProcessingManager.restoreInvokedToBeforeProcessing(invokedFileManager);
		ProcessingManager.restoreTestMethodToBeforeProcessing(testMethodFileManager);
		
		CollectorContainer.restoreCollectorInvocationLine();
		
		alreadyChanged.clear();
	}

	private void processInvokedMethod(InvokedContainer collector,
									  FileManager invokedFileManager,
									  FileManager testMethodFileManager) throws IOException {
		Logger.info("Processing source file of invoked - " 
				+ collector.getInvokedInfo().getConcreteInvokedSignature() 
				+ "..."
		);
		
		ProcessingManager.doProcessingInInvoked(invokedFileManager, testMethodFileManager);
		
		updateInvocationLineAfterInvokedProcessing(collector);
		
		Logger.info("Processing completed");
	}

	private void updateInvocationLineAfterInvokedProcessing(InvokedContainer collector) {
		if (App.isTestMode()) {
			if (collector.getInvokedInfo().getSrcPath().equals(
					collector.getTestMethodInfo().getSrcPath())) {
				updateCollector(collector, InvokedFileProcessor.getMapping());
			}
		}
		else {
			updateCollectors(
					InvokedFileProcessor.getMapping(),
					collector.getTestMethodInfo().getSrcPath(), 
					collector.getInvokedInfo().getSrcPath()
			);
		}
	}

	private void updateCollector(InvokedContainer collector, Map<Integer, Integer> mapping) {
		int invocationLine = collector.getInvokedInfo().getInvocationLine();
		
		if (mapping.containsKey(invocationLine))
			collector.getInvokedInfo().setInvocationLine(mapping.get(invocationLine));
	}

	private void updateCollectors(Map<Integer, Integer> mapping, Path testMethodSrcPath,
								  Path invokedSrcPath) {
		if (alreadyChanged.contains(testMethodSrcPath.toString()) && 
				!invokedSrcPath.equals(testMethodSrcPath))
			return;

		CollectorContainer.updateCollectorInvocationLines(
				mapping, 
				testMethodSrcPath
		);
		
		alreadyChanged.add(testMethodSrcPath.toString());
	}

	private void processTestMethod(InvokedContainer collector, 
								   FileManager testMethodFileManager) throws IOException {
		Logger.info(
				"Processing source file of test method "
				+ collector.getTestMethodInfo().getConcreteInvokedSignature() 
				+ "..."
		);
		
		ProcessingManager.doProcessingInTestMethod(testMethodFileManager);
		
		updateInvocationLineAfterTestMethodProcessing(collector);
		
		Logger.info("Processing completed");
	}

	private void updateInvocationLineAfterTestMethodProcessing(InvokedContainer collector) {
		if (App.isTestMode()) {
			updateCollector(collector, TestMethodFileProcessor.getMapping());
		}
		else {
			updateCollectors(
					TestMethodFileProcessor.getMapping(),
					collector.getTestMethodInfo().getSrcPath(), 
					collector.getInvokedInfo().getSrcPath()
			);
		}
	}

	private FileManager createTestMethodFileManager(InvokedContainer collector) {
		return new FileManager.Builder()
				.srcPath(collector.getTestMethodInfo().getSrcPath())
				.binDirectory(collector.getTestMethodInfo().getClassDirectory())
				.classPackage(collector.getTestMethodInfo().getPackage())
				.backupExtensionName("testMethod.bkp")
				.fileParserFactory(new TestMethodFileProcessorFactory())
				.build();
	}

	private FileManager createInvokedFileManager(InvokedContainer collector) {
		return new FileManager.Builder()
				.srcPath(collector.getInvokedInfo().getSrcPath())
				.binDirectory(collector.getInvokedInfo().getClassDirectory())
				.classPackage(collector.getInvokedInfo().getPackage())
				.backupExtensionName("invoked.bkp")
				.fileParserFactory(new InvokedFileProcessorFactory())
				.build();
	}

	private void fixAnonymousClassSignature(InvokedInfo invokedInfo) {
		if (analyzer.getAnalyzedInvokedSignature().isBlank())
			return;
		
		if (!invokedInfo.getInvokedSignature().equals(analyzer.getAnalyzedInvokedSignature())) {
			invokedInfo.setInvokedSignature(analyzer.getAnalyzedInvokedSignature());
		}
	}

	private void runDebugger(InvokedContainer collector) 
			throws IOException, InterruptedByTimeoutException {
		Logger.info(
				"Computing test path of invoked " 
				+ collector.getInvokedInfo().getConcreteInvokedSignature() 
				+ "..."
		);
		
		analyzer = AnalyzerFactory.createStandardTestPathAnalyzer(
				collector.getInvokedInfo(), 
				collector.getTestMethodInfo()
		);
		analyzer.analyze();

		checkDebuggerTimeout();
	}

	private void checkDebuggerTimeout() throws InterruptedByTimeoutException {
		if (!Analyzer.checkTimeout())
			return;
		
		try {
			Thread.sleep(2000);
		} 
		catch (InterruptedException e) {
		}
		
		throw new InterruptedByTimeoutException();
	}

	private void dump() {
		Logger.debug(
				this.getClass(), 
				"collector: " + invokedCollector.toString()
		);
	}
	
	protected boolean isConstructor() {
		return false;
	}
	
	protected void storeTestPath(InvokedContainer invokedContainer) {
		if (!analyzer.hasTestPaths())
			return;
			
		for (List<Integer> testPath : analyzer.getTestPaths()) {	
			if (testPath.isEmpty())
				continue;
			
			if (computedTestPaths.containsKey(invokedContainer))
				storeExistingTestPath(invokedContainer, testPath);
			else	
				storeNewTestPath(invokedContainer, testPath);
		}
	}

	private void storeNewTestPath(InvokedContainer invokedContainer, 
								  List<Integer> testPath) {
		List<List<Integer>> testPaths = new ArrayList<>();
		testPaths.add(testPath);
	
		computedTestPaths.put(invokedContainer, testPaths);
	}

	private void storeExistingTestPath(InvokedContainer invokedContainer, 
									   List<Integer> testPath) {
		List<List<Integer>> testPaths = computedTestPaths.get(invokedContainer);
		testPaths.add(testPath);
	}
	
	
	

	/**
	 * Gets computed test path.It will return the following map:
	 * <ul>
	 * 	<li><b>Key:</b> Test method signature and invoked signature</li>
	 * 	<li><b>Value:</b> List of test paths</li>
	 * </ul>
	 * 
	 * @return		Computed test path
	 * 
	 * @implNote	It must only be called after method {@link #run()} has 
	 * been executed
	 */
	public Map<InvokedContainer, List<List<Integer>>> getTestPaths() {
		return computedTestPaths;
	}
	
	/**
	 * Gets a specific computed test path.
	 * 
	 * @param		testMethodSignature Test method signature
	 * @param		invokedSignature Invoked signature
	 * 
	 * @return		List of test paths for the specified invoked or empty list
	 * if specified invoked has not a test path
	 * 
	 * @implNote	It must only be called after method {@link #run()} has 
	 * been executed
	 */
	public List<List<Integer>> getTestPaths(InvokedContainer container) {
		if (computedTestPaths.isEmpty())
			return List.of(new ArrayList<Integer>(0));
		
		return computedTestPaths.get(container);
	}
	
	public ExportManager getExportManager() {
		return exportManager;
	}
}
