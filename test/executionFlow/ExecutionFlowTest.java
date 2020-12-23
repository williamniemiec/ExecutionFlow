package executionFlow;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import executionFlow.exporter.ExportManager;
import executionFlow.info.InvokedContainer;
import executionFlow.info.InvokedInfo;
import executionFlow.io.manager.FileManager;
import executionFlow.io.manager.FilesManager;
import executionFlow.io.manager.InvokedManager;
import executionFlow.io.processor.InvokedFileProcessor;
import executionFlow.io.processor.ProcessorType;
import executionFlow.io.processor.TestMethodFileProcessor;
import executionFlow.io.processor.factory.PreTestMethodFileProcessorFactory;
import executionFlow.util.logger.Logger;

public abstract class ExecutionFlowTest {
	
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	protected static FileManager testMethodFileManager;
	protected static FilesManager testMethodManager;
	protected static InvokedManager processingManager;
	private Path srcTestMethod;
	private Path binTestMethod;
	private String classSignature;
	private String pkgTestMethod;
	
	protected String testMethodSignature;
	protected Object[] paramValues;
	protected List<List<Integer>> testPaths;
	protected Class<?>[] paramTypes;
	protected int invocationLine;
	
	
	//-------------------------------------------------------------------------
	//		Constructor
	//-------------------------------------------------------------------------
	protected ExecutionFlowTest(String classSignature, String pkgTestMethod, Path srcTestMethod, 
								Path binTestMethod) {
		this.classSignature = classSignature;
		this.pkgTestMethod = pkgTestMethod;
		this.srcTestMethod = srcTestMethod;
		this.binTestMethod = binTestMethod;
	}
	
		
	//-------------------------------------------------------------------------
	//		Test hooks
	//-------------------------------------------------------------------------	
	@BeforeClass
	public static void control() {
		RemoteControl.open();
	}
	
	@AfterClass
	public static void restoreInvoked() throws ClassNotFoundException, IOException {
		if (!processingManager.isInvokedManagerInitialized())
			return;
		
		processingManager.restoreInvokedOriginalFiles();
		processingManager.deleteInvokedFileManagerBackup();
		processingManager.destroyInvokedManager();
		
		RemoteControl.close();
	}
	
	@After
	public void restoreTestMethod() throws ClassNotFoundException, IOException {
		if (!processingManager.isTestMethodManagerInitialized())
			return;
		
		processingManager.restoreTestMethodOriginalFiles();
		
		testMethodManager.restoreAll();
		
		processingManager.deleteTestMethodFileManagerBackup();
		testMethodManager.deleteBackup();
		testMethodManager = null;
		processingManager.destroyTestMethodManager();
		TestMethodFileProcessor.clearMapping();
		InvokedFileProcessor.clearMapping();
	}
	
	
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	protected List<List<Integer>> computeTestPath(Collection<InvokedContainer> collection, 
												  InvokedContainer container) {
		ExecutionFlow ef = new ConstructorExecutionFlow(processingManager, collection);
		ExportManager exporter = ef.getExportManager();
		
		exporter.disableCalledMethodsByTestedInvokedExport();
		exporter.disableProcesedSourceFileExport();
		exporter.disableTestPathExport();
		exporter.disableTestersExport();
		
		return ef.run().getTestPaths(container);
	}
	
	protected void assertTestPathIs(int... testPath) {
		assertEquals(Arrays.asList(testPath), testPaths);
	}

	protected void computeTestPathOf(String constructorSignature) {
		InvokedContainer container = new InvokedContainer(
				getConstructorInfo(paramValues, constructorSignature),
				getTestMethodInfo(testMethodSignature)
		);
		
		String key = constructorSignature + Arrays.toString(paramValues);
		
		Map<String, InvokedContainer> constructorCollector = new LinkedHashMap<>();
		constructorCollector.put(key, container);
		
		testPaths = computeTestPath(constructorCollector.values(), container);
	}

	protected InvokedInfo getConstructorInfo(Object[] paramValues, String signature) {		
		return new InvokedInfo.Builder()
			.binPath(Path.of("bin/examples/complexTests/TestClass_ComplexTests.class"))
			.srcPath(Path.of("examples/examples/complexTests/TestClass_ComplexTests.java"))
			.invokedSignature(signature)
			.isConstructor(true)
			.parameterTypes(paramTypes)
			.args(paramValues)
			.invocationLine(invocationLine)
			.build();
	}

	protected InvokedInfo getTestMethodInfo(String testMethodSignature) {
		return new InvokedInfo.Builder()
			.binPath(binTestMethod)
			.srcPath(srcTestMethod)
			.invokedSignature(testMethodSignature)
			.build();
	}
	
	protected void initializeTest(String testMethodSignature) 
			throws ClassNotFoundException, IOException {
		init(testMethodSignature, (Object[]) null);
	}
	
	/**
	 * @throws		IOException If an error occurs during file parsing
	 * @throws		ClassNotFoundException If class {@link FileManager} was not
	 * found
	 */
	protected void init(String testMethodSignature, Object... testMethodArgs) 
			throws IOException, ClassNotFoundException {
		initializeProcessingManager();		
		onShutdown();
		initializeTestMethodManager(testMethodSignature, srcTestMethod, binTestMethod, 
									pkgTestMethod, testMethodArgs);
		doPreprocessing();
	}

	private void initializeProcessingManager() 
			throws ClassNotFoundException, IOException {
		processingManager = new InvokedManager(false);
		processingManager.destroyTestMethodManager();
		processingManager = new InvokedManager(true);
	}

	private void initializeTestMethodManager(String testMethodSignature, 
											 Path srcTestMethod, Path binTestMethod,
											 String packageTestMethod, 
											 Object... testMethodArgs) 
			 throws ClassNotFoundException, IOException {
		testMethodManager = new FilesManager(ProcessorType.PRE_TEST_METHOD, false);
		
		if (testMethodArgs == null) {
			testMethodFileManager = new FileManager.Builder()
					.srcPath(srcTestMethod)
					.binDirectory(InvokedInfo.getCompiledFileDirectory(binTestMethod))
					.classPackage(packageTestMethod)
					.backupExtensionName("pretestMethod.bkp")
					.fileParserFactory(new PreTestMethodFileProcessorFactory(testMethodSignature))
					.build();
		}
		else {
			testMethodFileManager = new FileManager.Builder()
					.srcPath(srcTestMethod)
					.binDirectory(InvokedInfo.getCompiledFileDirectory(binTestMethod))
					.classPackage(packageTestMethod)
					.backupExtensionName("pretestMethod.bkp")
					.fileParserFactory(new PreTestMethodFileProcessorFactory(testMethodSignature, testMethodArgs))
					.build();
		}
	}

	private void doPreprocessing() throws IOException {
		try {
			Logger.info("Pre-processing test method...");
			
			testMethodManager.processFile(testMethodFileManager);
			testMethodManager.compile(testMethodFileManager);
			
			Logger.info("Pre-processing completed");
		} 
		catch (IOException e) {
			testMethodManager.restoreAll();
			testMethodManager.deleteBackup();
			
			throw e;
		}
	}

	private void onShutdown() {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				try {
					restoreTestMethod();
					restoreInvoked();
				} 
				catch (ClassNotFoundException | IOException e) {
				}
			}
		});
	}
	
	/**
	 * @param		classSignature Test class signature
	 * @param		testMethodSignature Test method signature
	 * @param		srcTestMethod Test method compiled file path
	 * @param		binTestMethod Test method source file path
	 * @param		packageTestMethod Test method package
	 * @param		testMethodArgs Test method arguments (when it is a parameterized test)
	 * 
	 * @throws		IOException If an error occurs during file parsing
	 * @throws		ClassNotFoundException If class {@link FileManager} was not
	 * found
	 */
	protected void init(String classSignature, String testMethodSignature, 
			Path srcTestMethod, Path binTestMethod, String packageTestMethod) 
			throws IOException, ClassNotFoundException {
		init(classSignature, testMethodSignature, srcTestMethod, binTestMethod, 
				packageTestMethod, (Object[])null);
	}
}
