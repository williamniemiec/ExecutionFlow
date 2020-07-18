package executionFlow;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import executionFlow.exporter.*;
import executionFlow.info.CollectorInfo;
import executionFlow.io.FileManager;
import executionFlow.io.processor.factory.InvokedFileProcessorFactory;
import executionFlow.io.processor.factory.TestMethodFileProcessorFactory;
import executionFlow.util.ConsoleOutput;
import executionFlow.util.Pair;


/**
 * Generates data for collected constructors. Among these data:
 * <ul>
 * 	<li>Test path</li>
 * 	<li>Methods called by this method</li>
 * 	<li>Test methods that call this method</li>
 * </ul>
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		2.0.0
 * @since		2.0.0
 */
public class ConstructorExecutionFlow extends ExecutionFlow
{
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	/**
	 * Stores information about collected constructors.
	 */
	private Collection<CollectorInfo> constructorCollector;
	private boolean exportCalledMethods;
	
	
	//-------------------------------------------------------------------------
	//		Initialization block
	//-------------------------------------------------------------------------
	/**
	 * Defines how the export will be done.
	 */
	{
		exporter = EXPORT.equals(TestPathExportType.CONSOLE) ? new ConsoleExporter() : 
			new FileExporter("results", true);
	}
	
	
	//-------------------------------------------------------------------------
	//		Constructors
	//-------------------------------------------------------------------------
	/**
	 * Computes test path for collected constructors. Using this constructor,
	 * methods called by tested constructor will be exported to a CSV file.
	 * 
	 * @param		constructorCollector Collected constructors from 
	 * {@link executionFlow.runtime.ConstructorCollector}
	 */
	public ConstructorExecutionFlow(Collection<CollectorInfo> constructorCollector)
	{
		this(constructorCollector, true);
	}
	
	/**
	 * Computes test path for collected constructors.
	 * 
	 * @param		constructorCollector Collected constructors from 
	 * {@link executionFlow.runtime.ConstructorCollector}
	 * @param		exportCalledMethods If true, signature of methods called by tested 
	 * constructor will be exported to a CSV file
	 */
	public ConstructorExecutionFlow(Collection<CollectorInfo> constructorCollector, boolean exportCalledMethods)
	{
		this.constructorCollector = constructorCollector;
		this.exportCalledMethods = exportCalledMethods;
		
		computedTestPaths = new HashMap<>();
	}
	
	
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	@Override
	public ExecutionFlow execute()
	{
		// -----{ DEBUG }-----
		if (DEBUG) {
			ConsoleOutput.showDebug("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
			ConsoleOutput.showDebug("CEF: " + constructorCollector.toString());
			ConsoleOutput.showDebug("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
		}
		// -----{ END DEBUG }-----
		
		if (constructorCollector == null || constructorCollector.isEmpty())
			return this;
		
		List<List<Integer>> tp;
		MethodsCalledByTestedInvokedExporter methodsCalledExporter = isDevelopment() ?
				new MethodsCalledByTestedInvokedExporter("MethodsCalledByTestedConstructor", "examples\\results") :
				new MethodsCalledByTestedInvokedExporter("MethodsCalledByTestedConstructor", "results");
		
		
		// Generates test path for each collected method
		for (CollectorInfo collector : constructorCollector) {
			// Checks if collected constructor is within test method
			if (collector.getConstructorInfo().getSrcPath().equals(collector.getTestMethodInfo().getSrcPath())) {
				ConsoleOutput.showError("ConstructorExecutionFlow - " + collector.getConstructorInfo().getInvokedSignature());
				ConsoleOutput.showError("The constructor to be tested cannot be within the test class");
				ConsoleOutput.showError("Anonymous classes are not supported");
				ConsoleOutput.showError("This test path will be skipped");
				continue;
			}

			// Gets FileManager for method file
			FileManager constructorFileManager = new FileManager(
				collector.getConstructorInfo().getSrcPath(), 
				collector.getConstructorInfo().getClassDirectory(),
				collector.getConstructorInfo().getPackage(),
				new InvokedFileProcessorFactory()
			);
			
			// Gets FileManager for test method file
			FileManager testMethodFileManager = new FileManager(
				collector.getTestMethodInfo().getSrcPath(), 
				collector.getTestMethodInfo().getClassDirectory(),
				collector.getTestMethodInfo().getPackage(),
				new TestMethodFileProcessorFactory()
			);
			
			
			try {
				// Processes the source file of the test method if it has
				// not been processed yet
				if (!testMethodManager.wasParsed(testMethodFileManager)) {
					ConsoleOutput.showInfo("Processing source file of test method "
						+ collector.getTestMethodInfo().getInvokedSignature()+"...");
					
					testMethodManager.parse(testMethodFileManager).compile(testMethodFileManager);
					ConsoleOutput.showInfo("Processing completed");	
				}

				// Processes the source file of the method if it has not 
				// been processed yet
				if (!invokedManager.wasParsed(constructorFileManager)) {
					ConsoleOutput.showInfo("Processing source file of constructor " 
						+ collector.getConstructorInfo().getInvokedSignature()+"...");
					
					invokedManager.parse(constructorFileManager).compile(constructorFileManager);
					ConsoleOutput.showInfo("Processing completed");
				}
				
				// Computes test path from JDB
				ConsoleOutput.showInfo("Computing test path of constructor "
					+ collector.getConstructorInfo().getInvokedSignature()+"...");

				Analyzer analyzer = new Analyzer(collector.getConstructorInfo(), collector.getTestMethodInfo());					
				tp = analyzer.run().getTestPaths();
				
				if (tp.isEmpty() || tp.get(0).isEmpty())
					ConsoleOutput.showWarning("Test path is empty");
				else
					ConsoleOutput.showInfo("Test path has been successfully computed");
				
				// Stores each computed test path
				storeTestPath(tp, collector);
				
				// Exports called methods by tested constructor to a CSV
				if (exportCalledMethods) {
					methodsCalledExporter.export(collector.getConstructorInfo().getInvokedSignature(),
							analyzer.getMethodsCalledByTestedInvoked(), true);
				}
			} catch (Exception e) {
				ConsoleOutput.showError(e.getMessage());
				e.printStackTrace();
			}
		}
		
		return this;
	}
	
	@Override
	protected void storeTestPath(List<List<Integer>> testPaths, CollectorInfo collector)
	{
		List<List<Integer>> classPathInfo;
		Pair<String, String> signaturesInfo = new Pair<>(
			collector.getTestMethodInfo().getInvokedSignature(),
			collector.getConstructorInfo().getInvokedSignature() 
		);

		
		for (List<Integer> testPath : testPaths) {
			// Checks if test path belongs to a stored test method and constructor
			if (computedTestPaths.containsKey(signaturesInfo)) {
				classPathInfo = computedTestPaths.get(signaturesInfo);
				classPathInfo.add(testPath);
			} 
			// Else stores test path with its test method and constructor
			else {	
				classPathInfo = new ArrayList<>();
				
				
				classPathInfo.add(testPath);
				computedTestPaths.put(signaturesInfo, classPathInfo);
			}
		}
		
		// If test path is empty, stores test method and constructor with an empty list
		if (testPaths.isEmpty() || testPaths.get(0).isEmpty()) {
			classPathInfo = new ArrayList<>();
			
			
			classPathInfo.add(new ArrayList<>());
			computedTestPaths.put(signaturesInfo, classPathInfo);
		}
	}
}
