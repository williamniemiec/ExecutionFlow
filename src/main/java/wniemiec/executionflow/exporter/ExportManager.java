package wniemiec.executionflow.exporter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;

import wniemiec.executionflow.App;
import wniemiec.executionflow.collector.parser.TestedInvokedParser;
import wniemiec.executionflow.exporter.file.ProcessedSourceFileExporter;
import wniemiec.executionflow.exporter.signature.MethodsCalledByTestedInvokedExporter;
import wniemiec.executionflow.exporter.signature.TestedInvokedExporter;
import wniemiec.executionflow.exporter.testpath.ConsoleExporter;
import wniemiec.executionflow.exporter.testpath.FileExporter;
import wniemiec.executionflow.exporter.testpath.TestPathExportType;
import wniemiec.executionflow.exporter.testpath.TestPathExporter;
import wniemiec.executionflow.invoked.Invoked;
import wniemiec.executionflow.invoked.TestedInvoked;
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
 * @version		7.0.0
 * @since		6.0.0
 */
public abstract class ExportManager {

	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	protected static TestPathExportType testPathExportType;
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
	//		Constructor
	//-------------------------------------------------------------------------
	protected ExportManager(boolean isDevelopment, boolean isConstructor) {
		this.isConstructor = isConstructor;
		this.outputDir = isDevelopment ? "examples\\results" : "results";

		if (testPathExportType == null)
			testPathExportType = TestPathExportType.FILE;
		
		initializeTestPathExporter();
		initializeTestersExporter();
		initializeProcessedSourceFileExporter();
		initializeMethodsCalledByTestedInvokedExporter();
	}
	
	
	//-------------------------------------------------------------------------
	//		Factories
	//-------------------------------------------------------------------------
	public static ExportManager getMethodExportManager(boolean isDevelopment) {
		return new MethodExportManager(isDevelopment);
	}
	
	public static ExportManager getConstructorExportManager(boolean isDevelopment) {
		return new ConstructorExportManager(isDevelopment);
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
				new File(App.getCurrentProjectRoot().toFile(), outputDir)
		);
		
		this.mcutmAll = new TestedInvokedExporter(
				"MCUTM-ALL", 
				new File(App.getCurrentProjectRoot().toFile(), outputDir)
		);
	}
	
	private void initializeTestPathExporter() {
		this.testPathExporter = testPathExportType.equals(TestPathExportType.CONSOLE)
				? new ConsoleExporter() 
				: new FileExporter(outputDir, isConstructor);
	}
	
	private void initializeMethodsCalledByTestedInvokedExporter() {
		this.mcti = new MethodsCalledByTestedInvokedExporter(
				"MCTI", 
				outputDir
		);
	}
	
	public void exportAllMethodsAndConstructorsUsedInTestMethods(Set<TestedInvoked> 
	 															 invokedSet) {
		if (!exportTesters)
			return;
		
		mcutmAll.export(invokedSet);
	}
	
	protected void exportResultsFromParser(TestedInvokedParser parser) {
		exportTestPaths(parser.getTestPaths());
		exportEffectiveMethodsAndConstructorsUsedInTestMethods(
				parser.getMethodsAndConstructorsUsedInTestMethod()
		);
		exportProcessedSourceFiles(parser.getProcessedSourceFiles());
		exportMethodsCalledByTestedInvoked(
				parser.getMethodsCalledByTestedInvoked()
		);
	}
	
	public void exportTestPaths(Map<TestedInvoked, List<List<Integer>>> testPaths) {
		if (!exportTestPaths)
			return;
		
		testPathExporter.export(testPaths);
	}
	
	public void exportEffectiveMethodsAndConstructorsUsedInTestMethods(Set<TestedInvoked> 
																	   invokedSet) {
		if (!exportTesters)
			return;
		
		mcutmEffective.export(invokedSet);
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
			Map<Invoked, Set<String>> methodsCalledByTestedInvoked) {
		if (!exportCalledMethods)
			return;
		
		try {
			mcti.export(methodsCalledByTestedInvoked);
		} 
		catch (IOException e) {
			Logger.error(e.getMessage());
		}
	}
	
	public abstract void exportAllInvokedUsedInTestMethods();
	
	public abstract void exportAll();
	
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
	
	public static void setTestPathExportType(TestPathExportType type) {
		testPathExportType = type;
	}
}
