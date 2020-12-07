package executionFlow;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.channels.InterruptedByTimeoutException;
import java.nio.charset.Charset;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import executionFlow.analyzer.Analyzer;
import executionFlow.exporter.file.ProcessedSourceFileExporter;
import executionFlow.exporter.signature.MethodsCalledByTestedInvokedExporter;
import executionFlow.exporter.testpath.TestPathExporter;
import executionFlow.exporter.testpath.TestPathExportType;
import executionFlow.info.CollectorInfo;
import executionFlow.info.ConstructorInvokedInfo;
import executionFlow.info.InvokedInfo;
import executionFlow.info.MethodInvokedInfo;
import executionFlow.io.FileEncoding;
import executionFlow.io.FileManager;
import executionFlow.io.FilesManager;
import executionFlow.io.ProcessorType;
import executionFlow.io.processor.InvokedFileProcessor;
import executionFlow.io.processor.TestMethodFileProcessor;
import executionFlow.runtime.collector.TestMethodCollector;
import executionFlow.util.Logger;
import executionFlow.util.FileUtil;
import executionFlow.util.Pair;
import executionFlow.util.formatter.JavaIndenter;


/**
 * Generates data for each collected invoked, where an invoked can be a method
 * or a constructor. Among these data:
 * <ul>
 * 	<li>Test path</li>
 * 	<li>Methods called by this method</li>
 * 	<li>Test methods that call this method</li>
 * </ul>
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		5.2.0
 * @since		1.0
 */
@SuppressWarnings("unused")
public abstract class ExecutionFlow 
{
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	protected static final TestPathExportType EXPORT;
	
	/**
	 * Stores computed test paths from a class.<br />
	 * <ul>
	 * 	<li><b>Key:</b> {@link Pair} (test method signature, invoked signature)</li>
	 * 	<li><b>Value:</b> List of test paths</li>
	 * </ul>
	 */
	protected Map<Pair<String, String>, List<List<Integer>>> computedTestPaths;
	
	protected TestPathExporter exporter;
	protected MethodsCalledByTestedInvokedExporter invokedMethodsExporter;
	protected  ProcessedSourceFileExporter processedSourceFileExporter;
	protected boolean exportCalledMethods;
	
	/**
	 * Sets if environment is development. This will affect
	 * {@link #getAppRootPath()} and 
	 * {@link executionFlow.io.FileCompiler#compile()}.
	 */
	private static final boolean DEVELOPMENT;
	
	/**
	 * Manages test method files.
	 */
	private static FilesManager testMethodManager;
	
	/**
	 * Manages invoked files.
	 */
	private static FilesManager invokedManager;
	
	/**
	 * Path of application libraries.
	 */
	private static Path libPath;
	
	private static Path appRoot;
	private static Path currentProjectRoot;

	
	//-------------------------------------------------------------------------
	//		Initialization block
	//-------------------------------------------------------------------------	
	/**
	 * Sets environment. If the code is executed outside project, that is,
	 * through a jar file, it must be false.
	 */
	static {
		DEVELOPMENT = false;
	}
	
	/**
	 * Sets test path export type.
	 */
	static {
//		EXPORT = TestPathExportType.CONSOLE;
		EXPORT = TestPathExportType.FILE;
	}
	

	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	/**
	 * Initializes invoked managers. If some error occurs, should stop the
	 * application execution; otherwise, the original files that have been 
	 * modified in the last run may be lost.
	 * 
	 * @param		restoreOriginalFiles If true and if there are backup files,
	 * restore them
	 * @throws		ClassNotFoundException If FileManager class has not been found
	 * @throws		IOException If backup files could not be restored
	 */
	public static void init(boolean restoreOriginalFiles) throws ClassNotFoundException, IOException
	{
		if (testMethodManager == null) {
			try {
				testMethodManager = new FilesManager(ProcessorType.TEST_METHOD, true, restoreOriginalFiles);
			} 
			catch (ClassNotFoundException e) {
				throw new ClassNotFoundException("Class FileManager not found");
			} 
			catch (IOException e) {
				throw new IOException(
						"Could not recover the backup file of the test method\n"
						+ "See more: https://github.com/williamniemiec/"
						+ "ExecutionFlow/wiki/Solu%C3%A7%C3%A3o-de-problemas"
						+ "#could-not-recover-all-backup-files"
				);
			}
		}

		if (invokedManager == null) {
			try {
				invokedManager = new FilesManager(ProcessorType.INVOKED, true, restoreOriginalFiles);
				
				// Loads files that have already been processed
				if (!restoreOriginalFiles)
					invokedManager.load();
			} 
			catch (ClassNotFoundException e) {
				throw new ClassNotFoundException("Class FileManager not found");
			} 
			catch (IOException e) {
				throw new IOException(
						"Could not recover all backup files for methods\n"
						+ "See more: https://github.com/williamniemiec/"
						+ "ExecutionFlow/wiki/Solu%C3%A7%C3%A3o-de-problemas"
						+ "#could-not-recover-all-backup-files"
				);
			}
		}
	}
	
	/**
	 * Sets {@link #testMethodManager} to null.
	 */
	public static void destroyTestMethodManager()
	{
		testMethodManager = null;
	}
	
	/**
	 * Sets {@link #invokedManager} to null.
	 */
	public static void destroyInvokedManager()
	{
		invokedManager = null;
	}
	
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
	 * Walks the invoked recording its test paths and save the result in
	 * {@link #computedTestPaths}.
	 * 
	 * @return		This object to allow chained calls
	 */
	public abstract ExecutionFlow execute();
	
	/**
	 * Exports the result.
	 * 
	 * @throws		IllegalArgumentException If exporter is null
	 */
	public void export() 
	{
		if (exporter == null)
			throw new IllegalArgumentException("Exporter cannot be null");
		
		exporter.export(computedTestPaths);
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
	protected List<List<Integer>> run(InvokedInfo testMethodInfo, FileManager testMethodFileManager, 
			InvokedInfo invokedInfo, FileManager invokedFileManager) throws IOException, InterruptedByTimeoutException
	{
		List<List<Integer>> tp = new ArrayList<>();
		Analyzer analyzer;
		String invokedSignature = invokedInfo.getInvokedSignature().replaceAll("\\$", ".");
		boolean isConstructor = invokedInfo instanceof ConstructorInvokedInfo;
	
		
		analyzer = analyze(
				testMethodInfo, testMethodFileManager, 
				invokedInfo, invokedFileManager
		);
		
		// Exports processed file
		processedSourceFileExporter.export(
				invokedInfo.getSrcPath(), 
				invokedSignature,
				isConstructor
		);
		
		// Computes test path from JDB
		Logger.info("Computing test path of invoked " 
				+ invokedSignature + "...");
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
		
		tp = analyzer.getTestPaths();
		
		if (tp.isEmpty() || tp.get(0).isEmpty())
			Logger.warning("Test path is empty");
		else
			Logger.info("Test path has been successfully computed");				

		// Fix anonymous class signature
		if (isConstructor) {
			if (invokedInfo.getInvokedSignature() != analyzer.getAnalyzedInvokedSignature()) {
				if (analyzer.getAnalyzedInvokedSignature().isBlank()) {
					((ConstructorInvokedInfo)invokedInfo)
						.setInvokedSignature(invokedInfo.getInvokedSignature().replaceAll("\\$", "."));
				}
				else {
					((ConstructorInvokedInfo)invokedInfo)
						.setInvokedSignature(analyzer.getAnalyzedInvokedSignature());
				}
			}
		}
		else {
			invokedSignature = ((MethodInvokedInfo)invokedInfo).getConcreteMethodSignature();
			invokedSignature = invokedSignature.replaceAll("\\$", ".");
		}
		
		// Stores each computed test path
		storeTestPath(tp, Pair.of(
				testMethodInfo.getInvokedSignature(),
				invokedSignature
		));
		
		// Exports methods called by tested invoked to a CSV
		if (exportCalledMethods) {
			invokedMethodsExporter.export(
					invokedSignature, 
					analyzer.getMethodsCalledByTestedInvoked(), false
			);
		}

		analyzer.deleteMethodsCalledByTestedInvoked();

		return tp;
	}
	
	/**
	 * Stores test paths for an invoked. The test paths are stored in 
	 * {@link #computedTestPaths}.
	 * 
	 * @param		testPaths Test paths of this invoked
	 * @param		signaturesInfo Informations about test method along with 
	 * the invoked
	 */
	protected void storeTestPath(List<List<Integer>> testPaths, Pair<String, String> signaturesInfo)
	{
		List<List<Integer>> classTestPathInfo;

		
		for (List<Integer> testPath : testPaths) {
			// Checks if test path belongs to a stored test method and invoked
			if (computedTestPaths.containsKey(signaturesInfo)) {
				classTestPathInfo = computedTestPaths.get(signaturesInfo);
				classTestPathInfo.add(testPath);
			} 
			// Else stores test path with its test method and invoked
			else {	
				classTestPathInfo = new ArrayList<>();
				classTestPathInfo.add(testPath);
				computedTestPaths.put(signaturesInfo, classTestPathInfo);
			}
		}
		
		// If test path is empty, stores test method and invoked with an empty list
		if (testPaths.isEmpty() || testPaths.get(0).isEmpty()) {
			classTestPathInfo = new ArrayList<>();
			classTestPathInfo.add(new ArrayList<>());
			computedTestPaths.put(signaturesInfo, classTestPathInfo);
		}
	}
	
	/**
	 * Runs {@link Analyzer} on an invoked.
	 * 
	 * @param		testMethodInfo Test method information
	 * @param		testMethodFileManager Test method file information
	 * @param		invokedInfo Invoked information
	 * @param		invokedFileManager Invoked file information
	 * @param		collectors Information about all invoked collected
	 * 
	 * @return		Analyzer after finishing its execution
	 * 
	 * @throws		IOException If an error occurs during processing
	 * @throws		IllegalStateException If test method manager is null
	 * 
	 * @implNote	This method will instantiate an Analyzer and call the 
	 * {@link Analyzer#run()} method
	 */
	protected Analyzer analyze(InvokedInfo testMethodInfo, FileManager testMethodFileManager, 
			InvokedInfo invokedInfo, FileManager invokedFileManager) throws IOException
	{
		if (testMethodManager == null)
			throw new IllegalStateException("testMethodManager cannot be null. "
					+ "Make sure that the 'init' method has been called before.");
	
		String invSig = invokedInfo.getInvokedSignature().replaceAll("\\$", ".");
		Path testMethodSrcFile = testMethodInfo.getSrcPath();
		
		
		processTestMethod(testMethodInfo, testMethodFileManager);
		processInvoked(testMethodFileManager, invokedFileManager, invSig);
		
		updateInvokedInfo(invokedInfo, TestMethodFileProcessor.getMapping());
		
		if (invokedInfo.getSrcPath().equals(testMethodSrcFile)) {
			updateInvokedInfo(invokedInfo, InvokedFileProcessor.getMapping());
		}		
		
		return Analyzer.createStandardTestPathAnalyzer(invokedInfo, testMethodInfo);
	}
	
	/**
	 * Processes test method source file.
	 * 
	 * @param		testMethodInfo Test method to be processed
	 * @param		testMethodFileManager Test method file manager
	 * 
	 * @throws		IOException If an error occurs during processing or 
	 * compilation
	 */
	private void processTestMethod(InvokedInfo testMethodInfo, FileManager testMethodFileManager) throws IOException 
	{
		if (!testMethodManager.wasProcessed(testMethodFileManager)) {
			Logger.info("Processing source file of test method "
				+ testMethodInfo.getInvokedSignature().replaceAll("\\$", ".") + "...");
			
			try {
				testMethodManager.parse(testMethodFileManager).compile(testMethodFileManager);
			}
			catch (java.lang.NoClassDefFoundError e) {
				Logger.error("Process test method - " + e.getMessage());
				e.printStackTrace();
				System.exit(-1);
			}
			
			Logger.info("Processing completed");	
		}
	}
	
	/**
	 * Processes invoked source file.
	 * 
	 * @param		testMethodFileManager Test method to be processed
	 * @param		invokedFileManager Invoked file manager
	 * @param		invSig
	 * 
	 * @throws		IOException If an error occurs during processing or 
	 * compilation
	 */
	private void processInvoked(FileManager testMethodFileManager, FileManager invokedFileManager,
			String invSig) throws IOException 
	{
		if (!invokedManager.wasProcessed(invokedFileManager)) {
			boolean autoRestore = 
					!testMethodFileManager.getSrcFile().equals(invokedFileManager.getSrcFile());
			
			
			Logger.info("Processing source file of invoked - " 
				+ invSig + "...");
			
			invokedManager.parse(
					invokedFileManager,
					autoRestore
			).compile(invokedFileManager);
			
			Logger.info("Processing completed");
		}
	}
	
	/**
	 * Updates the invocation line of an invoked based on a mapping.
	 * 
	 * @param		invokedInfo Invoked to be updated
	 * @param		mapping Mapping that will be used as base for the update
	 */
	private void updateInvokedInfo(InvokedInfo invokedInfo, Map<Integer, Integer> mapping)
	{
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
		if (currentProjectRoot != null)
			return currentProjectRoot;
		
		File tmpFile;
		String[] allFiles;
		boolean hasSrcFolder = false;
		int i=0;
		
		
		tmpFile = new File(System.getProperty("user.dir"));
		
		// Searches for a path containing a directory named 'src'
		while (!hasSrcFolder) {
			allFiles = tmpFile.list();
			
			// Checks the name of every file in current path
			i=0;
			while (!hasSrcFolder && i < allFiles.length) {
				// If there is a directory named 'src' stop the search
				if (allFiles[i].equals("src")) {
					hasSrcFolder = true;
				} else {
					i++;
				}
			}
			// If there is not a directory named 'src', it searches in the 
			// parent folder
			if (!hasSrcFolder) {
				tmpFile = new File(tmpFile.getParent());
			}
		}
		
		currentProjectRoot = tmpFile.toPath();
		
		return currentProjectRoot;
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
		if (appRoot != null)
			return appRoot;
		
		try {
			File executionFlowBinPath = new File(ExecutionFlow.class
					.getProtectionDomain().getCodeSource().getLocation().toURI());
			appRoot = DEVELOPMENT ? executionFlowBinPath.getAbsoluteFile().getParentFile().toPath() : 
				executionFlowBinPath.getAbsoluteFile().toPath();

		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		
		return appRoot;
	}
	
	/**
	 * Finds directory of application libraries and stores it in {@link #libPath}.
	 * 
	 * @param		appRoot Application root path
	 * 
	 * @implSpec	Lazy initialization
	 */
	public static Path getLibPath()
	{
		if (libPath != null)
			return libPath;
		
		if (appRoot == null)
			getAppRootPath();
		
		libPath = Path.of(appRoot + "\\lib");

		return libPath;
	}
	
	public static FilesManager getTestMethodManager()
	{
		return testMethodManager;
	}
	
	public static FilesManager getInvokedManager()
	{
		return invokedManager;
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
	public Map<Pair<String, String>, List<List<Integer>>> getTestPaths()
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
	public List<List<Integer>> getTestPaths(String testMethodSignature, String invokedSignature)
	{
		return computedTestPaths.get(Pair.of(testMethodSignature, invokedSignature));
	}
	
	/**
	 * Changes exporter that will be used to export computed test path.
	 * 
	 * @param		exporter New exporter
	 * 
	 * @return		This object to allow chained calls
	 */
	public ExecutionFlow setExporter(TestPathExporter exporter) 
	{
		this.exporter = exporter;
		
		return this;
	}
}
