package executionFlow;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
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
import executionFlow.util.logger.LogLevel;
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
	private String pkgTestMethod;
	
	private String testMethodSignature;
	private Object[] paramValues;
	private List<List<Integer>> testPaths;
	private Class<?>[] paramTypes;
	private int invocationLine;
	private Object[] testMethodArgs;
	
	
	//-------------------------------------------------------------------------
	//		Constructor
	//-------------------------------------------------------------------------
	protected ExecutionFlowTest(/*String pkgTestMethod, Path srcTestMethod, 
								Path binTestMethod*/) {
//		this.pkgTestMethod = pkgTestMethod;
//		this.srcTestMethod = srcTestMethod;
//		this.binTestMethod = binTestMethod;
		this.testMethodArgs = (Object[]) null;
		this.paramTypes = new Class[] {};
		this.paramValues = new Object[] {};
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
	protected void withTestMethodSignature(String testMethodSignature) {
		this.testMethodSignature = testMethodSignature;
	}

//	@SuppressWarnings("rawtypes")
//	protected void withParameterTypes(Class... types) {
//		if (types.length == 0)
//			paramTypes = new Class[] {};
//		else
//			paramTypes = types;
//	}
//
//	protected void withParameterValues(Object... values) {
//		if (values.length == 0)
//			paramValues = new Object[] {};
//		else
//			paramValues = values;
//	}

	protected void invokedOnLine(int lineNumber) {
		if (lineNumber <= 0)
			throw new IllegalArgumentException("Invocation line must be greater than zero");
		
		this.invocationLine = lineNumber;
	}
	
	protected void withTestMethodParameterValues(Object... args) {
		this.testMethodArgs = args;
	}
	
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
	
	protected void assertTestPathIs(Integer... testPath) {
		assertEquals(List.of(Arrays.asList(testPath)), testPaths);
	}
	
	protected void assertTestPathIsEmpty() {
		assertEquals(List.of(Arrays.asList()), testPaths);
	}
	
	protected void assertTestPathIs(Integer[]... testPath) {
		assertEquals(fixIntegerVarArgs(testPath), testPaths);
	}
	
	private List<List<Integer>> fixIntegerVarArgs(Integer[]... varArgs) {
		List<List<Integer>> args = new ArrayList<>(varArgs.length);
		
		for (Integer[] tp : varArgs)
			args.add(Arrays.asList(tp));
		
		return args;
	}

	protected void computeTestPathOf(String invokedSignature) {
		InvokedContainer container = new InvokedContainer(
				getInvokedInfo(invokedSignature),
				getTestMethodInfo(testMethodSignature)
		);
		
		String key = invokedSignature + Arrays.toString(paramValues);
		
		Map<String, InvokedContainer> collector = new LinkedHashMap<>();
		collector.put(key, container);
		
		testPaths = computeTestPath(collector.values(), container);
	}

	protected InvokedInfo getInvokedInfo(String signature) {		
		return new InvokedInfo.Builder()
			.binPath(getBinTestedInvoked())
			.srcPath(getSrcTestedInvoked())
			.invokedSignature(signature)
			.isConstructor(isConstructor())
			.parameterTypes(paramTypes)
			.args(paramValues)
			.invocationLine(invocationLine)
			.build();
	}
	
	
	
	protected boolean isConstructor() {
		return false;
	}

	protected InvokedInfo getTestMethodInfo(String testMethodSignature) {
		return new InvokedInfo.Builder()
			.binPath(binTestMethod)
			.srcPath(srcTestMethod)
			.invokedSignature(testMethodSignature)
			.build();
	}
	
	/**
	 * @throws		IOException If an error occurs during file parsing
	 * @throws		ClassNotFoundException If class {@link FileManager} was not
	 * found
	 */
	protected void initializeTest() throws IOException, ClassNotFoundException {
		initializeFields();
		checkRequiredFields();
		
		initializeProcessingManager();		
		onShutdown();
		initializeTestMethodManager(testMethodSignature, srcTestMethod, binTestMethod, 
									pkgTestMethod);
		doPreprocessing();
	}

	private void initializeFields() {
		this.pkgTestMethod = getTestMethodPackage();
		this.binTestMethod = getTestMethodBinFile();
		this.srcTestMethod = getTestMethodSrcFile();
	}
	
	protected abstract String getTestMethodPackage();
	protected abstract Path getTestMethodBinFile();
	protected abstract Path getTestMethodSrcFile();
	protected abstract Path getBinTestedInvoked();
	protected abstract Path getSrcTestedInvoked();


	private void checkRequiredFields() {
		checkTestMethodSignature();
		checkInvocationLine();
	}

	private void checkTestMethodSignature() {
		if ((testMethodSignature == null) || testMethodSignature.isBlank())
			throw new IllegalStateException("Test method signature cannot be empty");
	}

	private void checkInvocationLine() {
		if (invocationLine <= 0)
			throw new IllegalStateException("Invocation line must be greater than zero");
	}

	private void initializeProcessingManager() 
			throws ClassNotFoundException, IOException {
		processingManager = new InvokedManager(false);
		processingManager.destroyTestMethodManager();
		processingManager = new InvokedManager(true);
	}

	private void initializeTestMethodManager(String testMethodSignature, 
											 Path srcTestMethod, Path binTestMethod,
											 String packageTestMethod) 
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
	
	protected void enableDebug() {
		Logger.setLevel(LogLevel.DEBUG);
	}
}
