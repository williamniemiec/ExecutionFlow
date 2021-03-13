package wniemiec.app.executionflow.exporter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;

import wniemiec.app.executionflow.App;
import wniemiec.app.executionflow.collector.parser.TestedInvokedParser;
import wniemiec.app.executionflow.exporter.file.ProcessedSourceFileExporter;
import wniemiec.app.executionflow.exporter.signature.MethodsCalledByTestedInvokedExporter;
import wniemiec.app.executionflow.exporter.signature.TestedInvokedExporter;
import wniemiec.app.executionflow.exporter.testpath.ConsoleExporter;
import wniemiec.app.executionflow.exporter.testpath.FileExporter;
import wniemiec.app.executionflow.exporter.testpath.TestPathExportType;
import wniemiec.app.executionflow.exporter.testpath.TestPathExporter;
import wniemiec.app.executionflow.invoked.Invoked;
import wniemiec.app.executionflow.invoked.TestedInvoked;
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
	private static TestPathExporter testPathExporter;
	private MethodsCalledByTestedInvokedExporter mcti;
	private ProcessedSourceFileExporter processedSourceFileExporter;
	private TestedInvokedExporter mcutmEffective;
	private TestedInvokedExporter mcutmAll;
	private boolean exportTestPaths = true;
	private boolean exportCalledMethods = true;
	private boolean exportProcessedSourceFile = true;
	private boolean exportTesters = true;
	private static boolean isConstructor;
	private static String outputDir;
	
	
	//-------------------------------------------------------------------------
	//		Constructor
	//-------------------------------------------------------------------------
	protected ExportManager(boolean isDevelopment, boolean isConstructor) {
		ExportManager.isConstructor = isConstructor;
		outputDir = isDevelopment ? "examples\\results" : "results";

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
	
	private static void initializeTestPathExporter() {
		testPathExporter = testPathExportType.equals(TestPathExportType.CONSOLE)
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
		if (parser.getTestPaths().isEmpty())
			return;
		
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
	
	public abstract void parseAndExportAll();
	
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
		initializeTestPathExporter();
	}
}