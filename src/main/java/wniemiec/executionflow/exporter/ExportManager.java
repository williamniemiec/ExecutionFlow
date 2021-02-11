package wniemiec.executionflow.exporter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;

import wniemiec.executionflow.ExecutionFlow;
import wniemiec.executionflow.exporter.file.ProcessedSourceFileExporter;
import wniemiec.executionflow.exporter.signature.MethodsCalledByTestedInvokedExporter;
import wniemiec.executionflow.exporter.signature.TestedInvokedExporter;
import wniemiec.executionflow.exporter.testpath.ConsoleExporter;
import wniemiec.executionflow.exporter.testpath.FileExporter;
import wniemiec.executionflow.exporter.testpath.TestPathExportType;
import wniemiec.executionflow.exporter.testpath.TestPathExporter;
import wniemiec.executionflow.invoked.InvokedContainer;
import wniemiec.executionflow.invoked.InvokedInfo;
import wniemiec.util.logger.Logger;

/**
 * Responsible for performing the exports that the application performs, which 
 * are as follow:
 * <ul>
 * 	<li>Test paths</li>
 * 	<li>Processed source files</li>
 * 	<li>Methods called by tested invoked</li>
 * 	<li>Test methods that use the tested invoked</li>
 * </ul>
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		6.0.5
 * @since		6.0.0
 */
public class ExportManager {

	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	protected static final TestPathExportType TEST_PATH_EXPORTER;
	private TestPathExporter testPathExporter;
	private MethodsCalledByTestedInvokedExporter mcti;
	private ProcessedSourceFileExporter processedSourceFileExporter;
	private TestedInvokedExporter mcutmEffective;
	private TestedInvokedExporter mcutmAll;
	private boolean exportTestPaths = true;
	private boolean exportCalledMethods = true;
	private boolean exportProcessedSourceFile = true;
	private boolean exportTesters = true;
	private boolean isConstructor;
	private String outputDir;
	
	
	//-------------------------------------------------------------------------
	//		Initialization block
	//-------------------------------------------------------------------------
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
	public ExportManager(boolean isDevelopment, boolean isConstructor) {
		this.isConstructor = isConstructor;
		this.outputDir = isDevelopment ? "examples\\results" : "results";
		
		initializeTestPathExporter();
		initializeTestersExporter();
		initializeProcessedSourceFileExporter();
		initializeMethodsCalledByTestedInvokedExporter();
	}
	
	
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	private void initializeProcessedSourceFileExporter() {
		this.processedSourceFileExporter = new ProcessedSourceFileExporter(
				outputDir, 
				isConstructor
		);
	}

	private void initializeTestersExporter() {		
		this.mcutmEffective = new TestedInvokedExporter(
				"MCUTM-EFFECTIVE", 
				new File(ExecutionFlow.getCurrentProjectRoot().toFile(), outputDir)
		);
		
		this.mcutmAll = new TestedInvokedExporter(
				"MCUTM-ALL", 
				new File(ExecutionFlow.getCurrentProjectRoot().toFile(), outputDir)
		);
	}
	
	private void initializeTestPathExporter() {
		this.testPathExporter = TEST_PATH_EXPORTER.equals(TestPathExportType.CONSOLE) ? 
				new ConsoleExporter() 
				: new FileExporter(outputDir, isConstructor);
	}
	
	private void initializeMethodsCalledByTestedInvokedExporter() {
		this.mcti = new MethodsCalledByTestedInvokedExporter(
				"MCTI", 
				outputDir
		);
	}
	
	public void exportEffectiveMethodsAndConstructorsUsedInTestMethods(Set<InvokedContainer> invokedSet) {
		if (!exportTesters)
			return;
		
		mcutmEffective.export(invokedSet);
	}
	
	public void exportAllMethodsAndConstructorsUsedInTestMethods(Set<InvokedContainer> invokedSet) {
		if (!exportTesters)
			return;
	
		mcutmAll.export(invokedSet);
	}
	
	public void exportTestPaths(Map<InvokedContainer, List<List<Integer>>> testPaths) {
		if (!exportTestPaths)
			return;
		
		testPathExporter.export(testPaths);
	}
	
	public void exportProcessedSourceFiles(Map<String, Path> processedSourceFiles) {
		if (!exportProcessedSourceFile)
			return;
		
		try {
			processedSourceFileExporter.export(processedSourceFiles);
		} 
		catch (IOException e) {
			Logger.error(e.getMessage());
		}
	}
	
	public void exportMethodsCalledByTestedInvoked(
			Map<InvokedInfo, Set<String>> methodsCalledByTestedInvoked) {
		if (!exportCalledMethods)
			return;
		
		try {
			mcti.export(methodsCalledByTestedInvoked);
		} 
		catch (IOException e) {
			Logger.error(e.getMessage());
		}
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
