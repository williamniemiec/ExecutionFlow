package wniemiec.app.java.executionflow.exporter;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import wniemiec.app.java.ExecutionFlow;
import wniemiec.app.java.executionflow.collector.parser.TestedInvokedParser;
import wniemiec.app.java.executionflow.exporter.signature.MethodsCalledByTestedInvokedExporter;
import wniemiec.app.java.executionflow.exporter.signature.TestedInvokedExporter;
import wniemiec.app.java.executionflow.exporter.testpath.ConsoleExporter;
import wniemiec.app.java.executionflow.exporter.testpath.FileExporter;
import wniemiec.app.java.executionflow.exporter.testpath.TestPathExportType;
import wniemiec.app.java.executionflow.exporter.testpath.TestPathExporter;
import wniemiec.app.java.executionflow.invoked.Invoked;
import wniemiec.app.java.executionflow.invoked.TestedInvoked;
import wniemiec.io.java.Consolex;

/**
 * Responsible for performing the exports that the application performs, which 
 * are as follow:
 * <ul>
 * 	<li>Test paths</li>
 * 	<li>Methods called by tested invoked</li>
 * 	<li>Test methods that use the tested invoked</li>
 * </ul>
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @since		6.0.0
 */
public abstract class ExportManager {

	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	protected static TestPathExportType testPathExportType;
	private static TestPathExporter testPathExporter;
	private MethodsCalledByTestedInvokedExporter mcti;
	private TestedInvokedExporter mcutmEffective;
	private TestedInvokedExporter mcutmAll;
	private boolean exportTestPaths = true;
	private boolean exportCalledMethods = true;
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
	
	private static void initializeTestPathExporter() {
		testPathExporter = testPathExportType.equals(TestPathExportType.CONSOLE)
				? new ConsoleExporter() 
				: new FileExporter(outputDir, isConstructor);
	}
	
	private void initializeMethodsCalledByTestedInvokedExporter() {
		this.mcti = new MethodsCalledByTestedInvokedExporter(
				"MCTMC", 
				outputDir
		);
	}
	
	public void exportAllMethodsAndConstructorsUsedInTestMethods(Set<TestedInvoked> 
	 															 invokedSet) {
		mcutmAll.export(invokedSet);
	}
	
	protected void exportResultsFromParser(TestedInvokedParser parser) {
		if (parser.getTestPaths().isEmpty())
			return;
		
		exportTestPaths(parser.getTestPaths());
		exportEffectiveMethodsAndConstructorsUsedInTestMethods(
				parser.getMethodsAndConstructorsUsedInTestMethod()
		);
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
	
	public void exportMethodsCalledByTestedInvoked(
			Map<Invoked, Set<String>> methodsCalledByTestedInvoked) {
		if (!exportCalledMethods)
			return;
		
		try {
			mcti.export(methodsCalledByTestedInvoked);
		} 
		catch (IOException e) {
			Consolex.writeError(e.getMessage());
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
