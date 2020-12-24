package executionflow.exporter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;

import executionflow.ExecutionFlow;
import executionflow.exporter.file.ProcessedSourceFileExporter;
import executionflow.exporter.signature.MethodsCalledByTestedInvokedExporter;
import executionflow.exporter.signature.TestedInvokedExporter;
import executionflow.exporter.testpath.ConsoleExporter;
import executionflow.exporter.testpath.FileExporter;
import executionflow.exporter.testpath.TestPathExportType;
import executionflow.exporter.testpath.TestPathExporter;
import executionflow.info.InvokedContainer;
import executionflow.util.logger.Logger;

public class ExportManager {

	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	protected static final TestPathExportType TEST_PATH_EXPORTER;
	private TestPathExporter testPathExporter;
	private MethodsCalledByTestedInvokedExporter mcti;
	private ProcessedSourceFileExporter processedSourceFileExporter;
	private TestedInvokedExporter testersExporter;
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
//		TEST_PATH_EXPORTER = TestPathExportType.CONSOLE;
		TEST_PATH_EXPORTER = TestPathExportType.FILE;
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
		this.testersExporter = new TestedInvokedExporter(
				"Testers", 
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
				outputDir,
				isConstructor
		);
	}
	
	public void exportTesters(Set<InvokedContainer> testers) {
		if (!exportTesters)
			return;
		
		testersExporter.export(testers);
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
			Map<String, Set<String>> methodsCalledByTestedInvoked) {
		if (!exportCalledMethods)
			return;
		
		try {
			mcti.export(methodsCalledByTestedInvoked);
		} 
		catch (IOException e) {
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
