package executionFlow;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.channels.InterruptedByTimeoutException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import executionFlow.analyzer.Analyzer;
import executionFlow.exporter.file.ProcessedSourceFileExporter;
import executionFlow.exporter.signature.MethodsCalledByTestedInvokedExporter;
import executionFlow.exporter.signature.TestedInvokedExporter;
import executionFlow.exporter.testpath.ConsoleExporter;
import executionFlow.exporter.testpath.FileExporter;
import executionFlow.exporter.testpath.TestPathExportType;
import executionFlow.exporter.testpath.TestPathExporter;
import executionFlow.info.InvokedContainer;
import executionFlow.info.InvokedInfo;
import executionFlow.io.manager.FileManager;
import executionFlow.io.manager.ProcessingManager;
import executionFlow.io.processor.InvokedFileProcessor;
import executionFlow.io.processor.TestMethodFileProcessor;
import executionFlow.io.processor.factory.InvokedFileProcessorFactory;
import executionFlow.io.processor.factory.TestMethodFileProcessorFactory;
import executionFlow.runtime.collector.TestMethodCollector;
import executionFlow.util.FileUtil;
import executionFlow.util.Pair;
import executionFlow.util.logger.Logger;

/**
 * For each collected method or constructor, obtain the following information:
 * <ul>
 * 	<li>Test path</li>
 * 	<li>Methods called by this method</li>
 * 	<li>Test methods that call this method</li>
 * </ul>
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		5.2.3
 * @since		1.0
 */
public abstract class ExecutionFlow {
	
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	protected static final TestPathExportType TEST_PATH_EXPORTER;
	
	/**
	 * Stores computed test paths from a class.<br />
	 * <ul>
	 * 	<li><b>Key:</b> {@link Pair} (test method signature, invoked signature)</li>
	 * 	<li><b>Value:</b> List of test paths</li>
	 * </ul>
	 */
//	protected Map<Pair<String, String>, List<List<Integer>>> computedTestPaths;
	protected Map<InvokedContainer, List<List<Integer>>> computedTestPaths;
	
	/**
	 * Sets if environment is development. This will affect
	 * {@link #getAppRootPath()} and 
	 * {@link executionFlow.io.compiler.aspectj.StandardAspectJCompiler#compile()}.
	 */
	private static final boolean DEVELOPMENT;
	
	private static Path appRoot;
	private static Path currentProjectRoot;
	private Analyzer analyzer;
	
	private TestPathExporter testPathExporter;
	protected MethodsCalledByTestedInvokedExporter invokedMethodsExporter;
	protected ProcessedSourceFileExporter processedSourceFileExporter;
	private TestedInvokedExporter testersExporter;

	private boolean exportTestPaths = true;
	private boolean exportCalledMethods = true;
	private boolean exportProcessedSourceFile = true;
	private boolean exportTesters = true;

	private ProcessingManager processingManager;


	
	//-------------------------------------------------------------------------
	//		Initialization block
	//-------------------------------------------------------------------------	
	/**
	 * Sets environment. If the code is executed outside project, that is,
	 * through a jar file, it must be false.
	 */
	static {
		DEVELOPMENT = true;
	}
	
	/**
	 * Sets test path export type.
	 */
	static {
		TEST_PATH_EXPORTER = TestPathExportType.CONSOLE;
//		TEST_PATH_EXPORTER = TestPathExportType.FILE;
	}
	
	
	//-------------------------------------------------------------------------
	//		Constructor
	//-------------------------------------------------------------------------	
	protected ExecutionFlow(ProcessingManager processingManager)
	{		
		this.processingManager = processingManager;
		
		initializeProcessedSourceFileExporter();
		initializeTestersExporter();
		initializeTestPathExporter();
	}


	private void initializeProcessedSourceFileExporter() {
		this.processedSourceFileExporter = isDevelopment() ? 
				new ProcessedSourceFileExporter("examples\\results", isConstructor())
				: new ProcessedSourceFileExporter("results", isConstructor());
	}


	private void initializeTestersExporter() {
		if (isDevelopment()) {
			this.testersExporter = new TestedInvokedExporter(
					"Testers", 
					new File(getCurrentProjectRoot().toFile(), "examples\\results")
			);
		}
		else {
			this.testersExporter = new TestedInvokedExporter(
					"Testers", 
					new File(getCurrentProjectRoot().toFile(), "results")
			);
		}
	}
	
	private void initializeTestPathExporter() {
		if (isDevelopment()) {
			testPathExporter = TEST_PATH_EXPORTER.equals(TestPathExportType.CONSOLE) ? 
					new ConsoleExporter() 
					: new FileExporter("examples\\results", isConstructor());
		}
		else {
			testPathExporter = TEST_PATH_EXPORTER.equals(TestPathExportType.CONSOLE) ? 
					new ConsoleExporter() 
					: new FileExporter("results", isConstructor());
		}
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
	public static boolean isDevelopment()
	{
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
	 * @param		testMethodInfo Test method information
	 * @param		testMethodFileManager Test method file information
	 * @param		invokedInfo Invoked information
	 * @param		invokedFileManager Invoked file information

	 * @return		Test path or empty list if there is no test path
	 * 
	 * @throws 		IOException  If an error occurs while computing test path 
	 * or exporting processed source file
	 * @throws 		InterruptedByTimeoutException If runtime has been exceeded 
	 */
	/**
	 * Walks the invoked recording its test paths and save the result in
	 * {@link #computedTestPaths}.
	 * 
	 * @return		This object to allow chained calls
	 */
	public final ExecutionFlow execute() {
		if ((getCollectors() == null) || getCollectors().isEmpty())
			return this;
		
		dumpCollectors();
		
		for (InvokedContainer collector : getCollectors()) {
			parseCollector(collector);
		}
		
		exportTestPaths();
		exportTesters();
		
		return this;
	}
	
	private void parseCollector(InvokedContainer collector) {
		FileManager invokedFileManager = createInvokedFileManager(collector);
		FileManager testMethodFileManager = createTestMethodFileManager(collector);
		
		try {
			processTestMethod(collector, testMethodFileManager);
			processInvokedMethod(collector, testMethodFileManager, invokedFileManager);
			
			runDebugger(collector);
			
			if (isConstructor())
				fixAnonymousClassSignature(collector.getInvokedInfo());					
			
			if (analyzer.hasTestPaths()) {
				exportProcessedSourceFile(collector.getInvokedInfo());
				exportMethodsCalledByTestedInvoked(collector.getInvokedInfo().getInvokedSignature());
				
				storeTestPath(
						new InvokedContainer(
								collector.getInvokedInfo(), 
								collector.getTestMethodInfo()
				));
//				storeTestPath(
//						collector.getTestMethodInfo().getInvokedSignature(), 
//						collector.getInvokedInfo().getConcreteInvokedSignature()
//				);
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
			processingManager.restoreOriginalFile(invokedFileManager);
			processingManager.restoreOriginalFile(testMethodFileManager);					
		}
	}
	
	private void exportTesters() {
		if (exportTesters)
			testersExporter.export(computedTestPaths.keySet());
	}

	private void processInvokedMethod(InvokedContainer collector, FileManager testMethodFileManager, 
			FileManager invokedFileManager) throws IOException {
		Logger.info("Processing source file of invoked - " 
				+ collector.getInvokedInfo().getConcreteInvokedSignature() + "...");
		
		processingManager.processInvoked(testMethodFileManager, invokedFileManager);
		
		Logger.info("Processing completed");
		
		if (collector.getInvokedInfo().getSrcPath().equals(collector.getTestMethodInfo().getSrcPath())) {
			TestMethodCollector.updateCollectorInvocationLines(
					InvokedFileProcessor.getMapping(), 
					collector.getTestMethodInfo().getSrcPath()
			);
		}	
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


	private FileManager createTestMethodFileManager(InvokedContainer collector) {
		FileManager testMethodFileManager;
		testMethodFileManager = new FileManager(
			collector.getTestMethodInfo().getClassSignature(),
			collector.getTestMethodInfo().getSrcPath(), 
			collector.getTestMethodInfo().getClassDirectory(),
			collector.getTestMethodInfo().getPackage(),
			new TestMethodFileProcessorFactory(),
			"testMethod.bkp"
		);
		return testMethodFileManager;
	}

	private FileManager createInvokedFileManager(InvokedContainer collector) {
			return new FileManager(
				collector.getInvokedInfo().getClassSignature(),
				collector.getInvokedInfo().getSrcPath(), 
				collector.getInvokedInfo().getClassDirectory(),
				collector.getInvokedInfo().getPackage(),
				new InvokedFileProcessorFactory(),
				"invoked.bkp"
			);
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
		
		analyzer = Analyzer.createStandardTestPathAnalyzer(
				collector.getInvokedInfo(), 
				collector.getTestMethodInfo()
		);
		analyzer.analyze();

		// Checks if time has been exceeded
		if (Analyzer.getTimeout()) {
			try {
				Thread.sleep(2000);
			} 
			catch (InterruptedException e) 
			{}
			
			throw new InterruptedByTimeoutException();
		}
	}

	protected abstract List<InvokedContainer> getCollectors();

	private void dumpCollectors() {
		Logger.debug(
				this.getClass().getName(), 
				"collector: " + getCollectors().toString()
		);
	}
	
	/**
	 * Exports test paths.
	 * 
	 * @throws		IllegalStateException If exporter is null
	 */
	private void exportTestPaths() 
	{
		if (!exportTestPaths)
			return;
		
		testPathExporter.export(computedTestPaths);
	}
	
	
	protected boolean isConstructor() {
		return false;
	}

	private void exportProcessedSourceFile(InvokedInfo invokedInfo) 
			throws IOException {
		if (!exportProcessedSourceFile)
			return;
		
		processedSourceFileExporter.export(
				invokedInfo.getSrcPath(), 
				invokedInfo.getConcreteInvokedSignature()
		);
	}

	private void exportMethodsCalledByTestedInvoked(String invokedSignature) {
		// Exports methods called by tested invoked to a CSV
		if (!exportCalledMethods || !analyzer.hasTestPaths())
			return;
		
		try {
			invokedMethodsExporter.export(
					invokedSignature, 
					analyzer.getMethodsCalledByTestedInvoked()
			);
		} catch (IOException e) {
		}

		analyzer.deleteMethodsCalledByTestedInvoked();
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
			
			if (computedTestPaths.containsKey(invokedContainer)) {
				storeExistingTestPath(invokedContainer, testPath);
			} 
			else {	
				storeNewTestPath(invokedContainer, testPath);
			}
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
		
		int currentInvocationLine = invokedInfo.getInvocationLine();
		

		if (mapping.containsKey(currentInvocationLine)) {
			invokedInfo.setInvocationLine(mapping.get(currentInvocationLine));
		}
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
	public static Path getCurrentProjectRoot()
	{
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
	public static Path getAppRootPath()
	{
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
	 * @implNote	It must only be called after method {@link #execute()} has 
	 * been executed
	 */
	public Map<InvokedContainer, List<List<Integer>>> getTestPaths()
	{
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
	 * @implNote	It must only be called after method {@link #execute()} has 
	 * been executed
	 */
	public List<List<Integer>> getTestPaths(InvokedContainer container)
	{
		return computedTestPaths.get(container);
	}
	
	public void enableTestPathExport() {
		exportTestPaths = true;
	}
	
	public void disableTestPathExport() {
		exportTestPaths = false;
	}
	
	public void enableCalledMethodsByTestedInvokedExport() {
		exportCalledMethods = true;
	}
	
	public void disableCalledMethodsByTestedInvokedExport() {
		exportCalledMethods = false;
	}
	
	public void enableProcesedSourceFileExport() {
		exportProcessedSourceFile = true;
	}
	
	public void disableProcesedSourceFileExport() {
		exportProcessedSourceFile = false;
	}
	
	public void enableTestersExport() {
		exportTesters = true;
	}
	
	public void disableTestersExport() {
		exportTesters = false;
	}
}
