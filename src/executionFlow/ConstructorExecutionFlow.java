package executionFlow;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import executionFlow.core.JDB;
import executionFlow.core.file.FileManager;
import executionFlow.core.file.parser.factory.InvokerFileParserFactory;
import executionFlow.core.file.parser.factory.TestMethodFileParserFactory;
import executionFlow.exporter.*;
import executionFlow.info.CollectorInfo;
import executionFlow.info.SignaturesInfo;
import executionFlow.util.ConsoleOutput;


/**
 * Computes test path for collected constructors.
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
	private boolean exportInvokedMethods;
	
	
	//-------------------------------------------------------------------------
	//		Initialization block
	//-------------------------------------------------------------------------
	/**
	 * Defines how the export will be done.
	 */
	{
		exporter = new ConsoleExporter();
		//exporter = new FileExporter("testPaths", true);
	}
	
	
	//-------------------------------------------------------------------------
	//		Constructors
	//-------------------------------------------------------------------------
	/**
	 * Computes test path for collected constructors. Using this constructor,
	 * the invoked methods by tested constructor will be exported to a CSV file.
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
	 * @param
	 */
	public ConstructorExecutionFlow(Collection<CollectorInfo> constructorCollector, boolean exportInvokedMethods)
	{
		this.constructorCollector = constructorCollector;
		this.exportInvokedMethods = exportInvokedMethods;
		
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
		
		List<List<Integer>> tp_jdb;
		InvokedMethodsByTestedInvokerExporter invokedMethodsExporter = 
				new InvokedMethodsByTestedInvokerExporter("InvokedMethodsByTestedConstructor", "testPaths");
		
		
		// Generates test path for each collected method
		for (CollectorInfo collector : constructorCollector) {
			// Checks if collected constructor is within test method
			if (collector.getConstructorInfo().getSrcPath().equals(collector.getTestMethodInfo().getSrcPath())) {
				ConsoleOutput.showError("ConstructorExecutionFlow - " + collector.getConstructorInfo().getInvokerSignature());
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
				new InvokerFileParserFactory()
			);
			
			// Gets FileManager for test method file
			FileManager testMethodFileManager = new FileManager(
				collector.getTestMethodInfo().getSrcPath(), 
				collector.getTestMethodInfo().getClassDirectory(),
				collector.getTestMethodInfo().getPackage(),
				new TestMethodFileParserFactory()
			);
			
			
			try {
				// Processes the source file of the test method if it has
				// not been processed yet
				if (!testMethodManager.wasParsed(testMethodFileManager)) {
					ConsoleOutput.showInfo("Processing source file of test method "
						+ collector.getTestMethodInfo().getInvokerSignature()+"...");
					
					testMethodManager.parse(testMethodFileManager).compile(testMethodFileManager);
					ConsoleOutput.showInfo("Processing completed");	
				}

				// Processes the source file of the method if it has not 
				// been processed yet
				if (!invokerManager.wasParsed(constructorFileManager)) {
					ConsoleOutput.showInfo("Processing source file of constructor " 
						+ collector.getConstructorInfo().getInvokerSignature()+"...");
					
					invokerManager.parse(constructorFileManager).compile(constructorFileManager);
					ConsoleOutput.showInfo("Processing completed");
				}
				
				// Computes test path from JDB
				ConsoleOutput.showInfo("Computing test path of constructor "
					+ collector.getConstructorInfo().getInvokerSignature()+"...");

				JDB jdb = new JDB();					
				tp_jdb = jdb.run(collector.getConstructorInfo(), collector.getTestMethodInfo()).getTestPaths();
				
				if (tp_jdb.isEmpty() || tp_jdb.get(0).isEmpty())
					ConsoleOutput.showWarning("Test path is empty");
				else
					ConsoleOutput.showInfo("Test path has been successfully computed");
				
				// Stores each computed test path
				storeTestPath(tp_jdb, collector);
				
				// Exports invoked methods by tested constructor to a CSV
				if (exportInvokedMethods) {
					invokedMethodsExporter.export(collector.getConstructorInfo().getInvokerSignature(),
							jdb.getInvokedMethodsByTestedInvoker(), true);
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
		SignaturesInfo signaturesInfo = new SignaturesInfo(
			collector.getConstructorInfo().getInvokerSignature(), 
			collector.getTestMethodInfo().getInvokerSignature()
		);

		
		for (List<Integer> testPath : testPaths) {
			// Checks if test path belongs to a stored test method and method
			if (computedTestPaths.containsKey(signaturesInfo)) {
				classPathInfo = computedTestPaths.get(signaturesInfo);
				classPathInfo.add(testPath);
			} 
			// Else stores test path with its test method and method
			else {	
				classPathInfo = new ArrayList<>();
				
				
				classPathInfo.add(testPath);
				computedTestPaths.put(signaturesInfo, classPathInfo);
			}
		}
		
		// If test path is empty, stores test method and invoker with an empty list
		if (testPaths.isEmpty() || testPaths.get(0).isEmpty()) {
			classPathInfo = new ArrayList<>();
			
			
			classPathInfo.add(new ArrayList<>());
			computedTestPaths.put(signaturesInfo, classPathInfo);
		}
	}
}
