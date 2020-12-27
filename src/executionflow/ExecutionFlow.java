package executionflow;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.channels.InterruptedByTimeoutException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import executionflow.analyzer.Analyzer;
import executionflow.analyzer.AnalyzerFactory;
import executionflow.exporter.ExportManager;
import executionflow.info.InvokedContainer;
import executionflow.info.InvokedInfo;
import executionflow.io.manager.FileManager;
import executionflow.io.manager.InvokedManager;
import executionflow.io.processor.factory.InvokedFileProcessorFactory;
import executionflow.io.processor.factory.TestMethodFileProcessorFactory;
import executionflow.io.processor.fileprocessor.InvokedFileProcessor;
import executionflow.io.processor.fileprocessor.TestMethodFileProcessor;
import executionflow.runtime.collector.TestMethodCollector;
import executionflow.util.FileUtil;
import executionflow.util.logger.Logger;

/**
 * For each collected method or constructor, obtain the following information:
 * <ul>
 * 	<li>Test path</li>
 * 	<li>Methods called by this method</li>
 * 	<li>Test methods that call this method</li>
 * </ul>
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		6.0.0
 * @since		1.0
 */
public abstract class ExecutionFlow {
	
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
	
	private static final boolean DEVELOPMENT;	
	private static Path appRoot;
	private static Path currentProjectRoot;
	private InvokedManager processingManager;
	private Analyzer analyzer;
	private Map<String, Path> processedSourceFiles;
	private ExportManager exportManager;

	
	//-------------------------------------------------------------------------
	//		Initialization block
	//-------------------------------------------------------------------------	
	/**
	 * Sets environment. If the code is executed outside project, that is,
	 * through a jar file, it must be false. It will affect
	 * {@link #getAppRootPath()} and 
	 * {@link executionflow.io.compiler.aspectj.StandardAspectJCompiler#compile()}.
	 */
	static {
		DEVELOPMENT = false;
	}
	
	
	//-------------------------------------------------------------------------
	//		Constructor
	//-------------------------------------------------------------------------	
	protected ExecutionFlow(InvokedManager processingManager) {		
		this.processingManager = processingManager;
		this.exportManager = new ExportManager(isDevelopment(), isConstructor());
		this.computedTestPaths = new HashMap<>();
		this.processedSourceFiles = new HashMap<>();
	}
	

	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	/**
	 * Checks if it is development environment. If it is production environment,
	 * it will return false; otherwise, true.
	 * 
	 * @return		If it is development environment
	 */
	public static boolean isDevelopment() {
		return DEVELOPMENT;
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
	 * 
	 * @return		Itself to allow chained calls
	 */
	public final ExecutionFlow run() {
		if ((getCollectors() == null) || getCollectors().isEmpty())
			return this;
		
		dump();
		
		for (InvokedContainer collector : getCollectors()) {
			parseCollector(collector);
		}
		
		export();
		
		return this;
	}
	
	private void export() {
		exportManager.exportTestPaths(computedTestPaths);
		exportManager.exportTesters(computedTestPaths.keySet());
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
			processInvokedMethod(collector, testMethodFileManager, invokedFileManager);
			
			runDebugger(collector);
			
			storeResults(collector);
		} 
		catch (InterruptedByTimeoutException e1) {
			Logger.error("Time exceeded");
		} 
		catch (IllegalStateException e2) {
			Logger.error(e2.getMessage());
		}
		catch (IOException e3) {
			Logger.error(e3.getMessage());
			
			processingManager.restoreOriginalFile(invokedFileManager);
			processingManager.restoreOriginalFile(testMethodFileManager);
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

	private void processInvokedMethod(InvokedContainer collector, 
									  FileManager testMethodFileManager, 
									  FileManager invokedFileManager) throws IOException {
		Logger.info("Processing source file of invoked - " 
				+ collector.getInvokedInfo().getConcreteInvokedSignature() 
				+ "..."
		);
		
		processingManager.processInvoked(testMethodFileManager, invokedFileManager);
		
		updateCollectors(
				collector.getTestMethodInfo().getSrcPath(), 
				collector.getInvokedInfo().getSrcPath()
		);
		
		Logger.info("Processing completed");
	}

	private void updateCollectors(Path testMethodSrcPath, Path invokedSrcPath) {
		if (!invokedSrcPath.equals(testMethodSrcPath))
			return;

		TestMethodCollector.updateCollectorInvocationLines(
				InvokedFileProcessor.getMapping(), 
				testMethodSrcPath
		);
	}

	private void processTestMethod(InvokedContainer collector, 
								   FileManager testMethodFileManager) throws IOException {
		Logger.info(
				"Processing source file of test method "
				+ collector.getTestMethodInfo().getConcreteInvokedSignature() 
				+ "..."
		);
		
		processingManager.processTestMethod(testMethodFileManager);
		
		updateInvocationLine(
				collector.getInvokedInfo(), 
				TestMethodFileProcessor.getMapping()
		);
		
		Logger.info("Processing completed");
	}
	
	/**
	 * Updates the invocation line of an invoked based on a mapping.
	 * 
	 * @param		invokedInfo Invoked to be updated
	 * @param		mapping Mapping that will be used as base for the update
	 */
	private void updateInvocationLine(InvokedInfo invokedInfo, 
									  Map<Integer, Integer> mapping) {
		if (mapping == null)
			return;

		if (mapping.containsKey(invokedInfo.getInvocationLine())) {
			invokedInfo.setInvocationLine(mapping.get(invokedInfo.getInvocationLine()));
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
		catch (InterruptedException e) 
		{}
		
		throw new InterruptedByTimeoutException();
	}

	protected abstract List<InvokedContainer> getCollectors();

	private void dump() {
		Logger.debug(
				this.getClass(), 
				"collector: " + getCollectors().toString()
		);
	}
	
	protected boolean isConstructor() {
		return false;
	}
	
	protected void storeTestPath(InvokedContainer invokedContainer) {
		if (analyzer.hasTestPaths())
			storeAllTestPaths(invokedContainer);
		else
			storeEmptyTestPath(invokedContainer);
	}
	
	private void storeAllTestPaths(InvokedContainer invokedContainer) {
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

	private void storeEmptyTestPath(InvokedContainer invokedContainer) {
		List<List<Integer>> classTestPathInfo = new ArrayList<>();
		classTestPathInfo.add(new ArrayList<>());
		
		computedTestPaths.put(invokedContainer, classTestPathInfo);
	}
	
	
	//-------------------------------------------------------------------------
	//		Getters & Setters
	//-------------------------------------------------------------------------
	/**
	 * Finds current project root (project that is running the application). It
	 * will return the path that contains a directory with name 'src'. 
	 * 
	 * @return		Project root path
	 * 
	 * @implSpec	Lazy initialization
	 */
	public static Path getCurrentProjectRoot() {
		if (currentProjectRoot == null)
			initializeCurrentProjectRoot();
		
		return currentProjectRoot;
	}
	
	private static void initializeCurrentProjectRoot() {
		File currentDirectory = new File(System.getProperty("user.dir"));
		
		currentProjectRoot = FileUtil.searchDirectory("src", currentDirectory).toPath();
	}

	/**
	 * Gets application root path, based on class {@link ExecutionFlow} location.
	 * 
	 * @return		Application root path
	 * 
	 * @implSpec	Lazy initialization
	 */
	public static Path getAppRootPath() {
		if (appRoot == null)
			initializeAppRoot();
		
		return appRoot;
	}
	
	private static void initializeAppRoot() {
		try {
			File executionFlowBinPath = new File(
					ExecutionFlow.class
						.getProtectionDomain()
						.getCodeSource()
						.getLocation()
						.toURI()
			);
			
			if (isDevelopment()) {
				appRoot = executionFlowBinPath
						.getAbsoluteFile()
						.getParentFile()
						.toPath();
			}
			else {
				appRoot = executionFlowBinPath
						.getAbsoluteFile()
						.toPath();
			}
		} 
		catch (URISyntaxException e) {
			Logger.error("Error initializing application root path");
			
			appRoot = null;
		}
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
